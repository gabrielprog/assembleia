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
        validCpf = "08223861941";
        invalidCpf = "12345678901";
        voteStatus = VoteStatus.YES;
        
        LocalDateTime now = LocalDateTime.now();
        
        activeSession = new Session(now.minusHours(1), now.plusHours(1));
        activeSession.setId(UUID.randomUUID());
        
        expiredSession = new Session(now.minusHours(3), now.minusHours(1));
        expiredSession.setId(UUID.randomUUID());
        
        futureSession = new Session(now.plusHours(1), now.plusHours(3));
        futureSession.setId(UUID.randomUUID());
        
        activeAgenda = new Agenda("Active Agenda", "Description", activeSession);
        activeAgenda.setId(agendaId);
        
        expiredAgenda = new Agenda("Expired Agenda", "Description", expiredSession);
        expiredAgenda.setId(UUID.randomUUID());
        
        futureAgenda = new Agenda("Future Agenda", "Description", futureSession);
        futureAgenda.setId(UUID.randomUUID());
    }

    @Test
    @DisplayName("Should prevent duplicate voting")
    void shouldPreventDuplicateVoting() {
        when(voteGateway.existsByAgendaIdAndCpf(agendaId, validCpf)).thenReturn(true);

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> voteUseCase.registerVote(activeAgenda, validCpf, voteStatus)
        );
        
        assertEquals("Participant has already voted on this agenda.", exception.getMessage());
        verify(eventProducer, never()).publishVoteRegisteredEvent(any());
    }

    @Test
    @DisplayName("Should reject votes for expired sessions")
    void shouldRejectVotesForExpiredSessions() {
        when(voteGateway.existsByAgendaIdAndCpf(expiredAgenda.getId(), validCpf)).thenReturn(false);
        
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> voteUseCase.registerVote(expiredAgenda, validCpf, voteStatus)
        );
        
        assertEquals("Voting session has ended.", exception.getMessage());
        verify(eventProducer, never()).publishVoteRegisteredEvent(any());
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
        when(voteGateway.existsByAgendaIdAndCpf(agendaId, validCpf)).thenReturn(false);
        
        Vote result = voteUseCase.registerVote(activeAgenda, validCpf, voteStatus);
        
        assertNotNull(result);
        assertEquals(agendaId, result.getAgenda().getId());
        assertEquals(validCpf, result.getCpf());
        assertEquals(voteStatus, result.getVote());
        assertNotNull(result.getDateTime());
        
        verify(voteGateway).existsByAgendaIdAndCpf(agendaId, validCpf);
        verify(eventProducer).publishVoteRegisteredEvent(any());
    }

    @Test
    @DisplayName("Should validate CPF correctly")
    void shouldValidateCpfCorrectly() {
        when(voteGateway.existsByAgendaIdAndCpf(agendaId, validCpf)).thenReturn(false);

        assertDoesNotThrow(() ->
            voteUseCase.registerVote(activeAgenda, validCpf, voteStatus)
        );
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> voteUseCase.registerVote(activeAgenda, invalidCpf, voteStatus)
        );
        
        assertEquals("Invalid CPF provided", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle formatted CPF correctly")
    void shouldHandleFormattedCpfCorrectly() {
        String formattedValidCpf = "082.238.619-41";
        when(voteGateway.existsByAgendaIdAndCpf(agendaId, formattedValidCpf)).thenReturn(false);
        
        assertDoesNotThrow(() -> 
            voteUseCase.registerVote(activeAgenda, formattedValidCpf, voteStatus)
        );
        
        String formattedInvalidCpf = "123.456.789-01";
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> voteUseCase.registerVote(activeAgenda, formattedInvalidCpf, voteStatus)
        );
        
        assertEquals("Invalid CPF provided", exception.getMessage());
    }
}
