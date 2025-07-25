package br.com.assembleia.assembleia.application.usecases;

import br.com.assembleia.assembleia.adapters.enums.VoteStatus;
import br.com.assembleia.assembleia.adapters.gateways.VoteGateway;
import br.com.assembleia.assembleia.adapters.gateways.AgendaGateway;
import br.com.assembleia.assembleia.infra.db.entities.Agenda;
import br.com.assembleia.assembleia.infra.db.entities.Session;
import br.com.assembleia.assembleia.infra.db.entities.Vote;
import br.com.assembleia.assembleia.infra.messaging.producers.AssembleiaEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VoteUseCase Business Logic Tests")
class VoteUseCaseBusinessLogicTest {

    @Mock
    private VoteGateway voteGateway;

    @Mock
    private AgendaGateway agendaGateway;

    @Mock
    private AssembleiaEventProducer eventProducer;

    @InjectMocks
    private VoteUseCase voteUseCase;

    private UUID agendaId;
    private String validCpf;
    private String invalidCpf;
    private VoteStatus voteStatus;
    private Session activeSession;
    private Session expiredSession;
    private Session futureSession;
    private Agenda activeAgenda;
    private Agenda expiredAgenda;
    private Agenda futureAgenda;

    @BeforeEach
    void setUp() {
        agendaId = UUID.randomUUID();
        validCpf = "11144477735"; // CPF válido
        invalidCpf = "12345678901"; // CPF inválido
        voteStatus = VoteStatus.YES;
        
        LocalDateTime now = LocalDateTime.now();
        
        // Sessão ativa (iniciada há 1 hora, termina em 1 hora)
        activeSession = new Session(now.minusHours(1), now.plusHours(1));
        
        // Sessão expirada (iniciada há 3 horas, terminou há 1 hora)
        expiredSession = new Session(now.minusHours(3), now.minusHours(1));
        
        // Sessão futura (inicia em 1 hora, termina em 3 horas)
        futureSession = new Session(now.plusHours(1), now.plusHours(3));
        
        activeAgenda = new Agenda("Active Agenda", "Description", activeSession);
        expiredAgenda = new Agenda("Expired Agenda", "Description", expiredSession);
        futureAgenda = new Agenda("Future Agenda", "Description", futureSession);
    }

    @Test
    @DisplayName("Should validate CPF correctly")
    void shouldValidateCpfCorrectly() {
        // Valid CPF should not throw exception
        when(voteGateway.existsByAgendaIdAndCpf(agendaId, validCpf)).thenReturn(false);
        
        assertDoesNotThrow(() -> 
            voteUseCase.registerVote(agendaId, activeAgenda, validCpf, voteStatus)
        );
        
        // Invalid CPF should throw exception
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> voteUseCase.registerVote(agendaId, activeAgenda, invalidCpf, voteStatus)
        );
        
        assertEquals("Invalid CPF provided", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject null parameters")
    void shouldRejectNullParameters() {
        // Null agendaId
        IllegalArgumentException exception1 = assertThrows(
            IllegalArgumentException.class,
            () -> voteUseCase.registerVote(null, activeAgenda, validCpf, voteStatus)
        );
        assertEquals("All fields must be filled.", exception1.getMessage());
        
        // Null CPF
        IllegalArgumentException exception2 = assertThrows(
            IllegalArgumentException.class,
            () -> voteUseCase.registerVote(agendaId, activeAgenda, null, voteStatus)
        );
        assertEquals("All fields must be filled.", exception2.getMessage());
        
        // Null vote
        IllegalArgumentException exception3 = assertThrows(
            IllegalArgumentException.class,
            () -> voteUseCase.registerVote(agendaId, activeAgenda, validCpf, null)
        );
        assertEquals("All fields must be filled.", exception3.getMessage());
    }

    @Test
    @DisplayName("Should prevent duplicate voting")
    void shouldPreventDuplicateVoting() {
        // Participant has already voted
        when(voteGateway.existsByAgendaIdAndCpf(agendaId, validCpf)).thenReturn(true);
        
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> voteUseCase.registerVote(agendaId, activeAgenda, validCpf, voteStatus)
        );
        
        assertEquals("Participant has already voted on this agenda.", exception.getMessage());
        verify(voteGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should reject votes for expired sessions")
    void shouldRejectVotesForExpiredSessions() {
        when(voteGateway.existsByAgendaIdAndCpf(agendaId, validCpf)).thenReturn(false);
        
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> voteUseCase.registerVote(agendaId, expiredAgenda, validCpf, voteStatus)
        );
        
        assertEquals("Voting session has ended.", exception.getMessage());
        verify(voteGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should check if participant has voted")
    void shouldCheckIfParticipantHasVoted() {
        when(voteGateway.existsByAgendaIdAndCpf(agendaId, validCpf)).thenReturn(true);
        
        boolean hasVoted = voteUseCase.hasVoted(agendaId, validCpf);
        
        assertTrue(hasVoted);
        verify(voteGateway).existsByAgendaIdAndCpf(agendaId, validCpf);
    }

    @Test
    @DisplayName("Should check if participant has not voted")
    void shouldCheckIfParticipantHasNotVoted() {
        when(voteGateway.existsByAgendaIdAndCpf(agendaId, validCpf)).thenReturn(false);
        
        boolean hasVoted = voteUseCase.hasVoted(agendaId, validCpf);
        
        assertFalse(hasVoted);
        verify(voteGateway).existsByAgendaIdAndCpf(agendaId, validCpf);
    }

    @Test
    @DisplayName("Should register valid vote successfully")
    void shouldRegisterValidVoteSuccessfully() {
        // Arrange
        when(voteGateway.existsByAgendaIdAndCpf(agendaId, validCpf)).thenReturn(false);
        
        // Act
        Vote result = voteUseCase.registerVote(agendaId, activeAgenda, validCpf, voteStatus);
        
        // Assert
        assertNotNull(result);
        assertEquals(activeAgenda, result.getAgenda());
        assertEquals(validCpf, result.getCpf());
        assertEquals(voteStatus, result.getVote());
        assertNotNull(result.getDateTime());
        
        verify(voteGateway).existsByAgendaIdAndCpf(agendaId, validCpf);
        verify(voteGateway).save(any(Vote.class));
        verify(eventProducer).publishVoteRegisteredEvent(any());
    }

    @Test
    @DisplayName("Should handle formatted CPF correctly")
    void shouldHandleFormattedCpfCorrectly() {
        String formattedValidCpf = "111.444.777-35"; // CPF válido formatado
        when(voteGateway.existsByAgendaIdAndCpf(agendaId, formattedValidCpf)).thenReturn(false);
        
        assertDoesNotThrow(() -> 
            voteUseCase.registerVote(agendaId, activeAgenda, formattedValidCpf, voteStatus)
        );
        
        String formattedInvalidCpf = "123.456.789-01"; // CPF inválido formatado
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> voteUseCase.registerVote(agendaId, activeAgenda, formattedInvalidCpf, voteStatus)
        );
        
        assertEquals("Invalid CPF provided", exception.getMessage());
    }
}
