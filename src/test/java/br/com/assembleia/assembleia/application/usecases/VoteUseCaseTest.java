package br.com.assembleia.assembleia.application.usecases;

import br.com.assembleia.assembleia.adapters.enums.VoteStatus;
import br.com.assembleia.assembleia.adapters.gateways.VoteGateway;
import br.com.assembleia.assembleia.infra.db.entities.Agenda;
import br.com.assembleia.assembleia.infra.db.entities.Session;
import br.com.assembleia.assembleia.infra.db.entities.Vote;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VoteUseCase Unit Tests")
class VoteUseCaseTest {

    @Mock
    private VoteGateway voteGateway;

    @InjectMocks
    private VoteUseCase voteUseCase;

    private UUID agendaId;
    private String cpf;
    private VoteStatus voteStatus;
    private Session session;
    private Agenda agenda;

    @BeforeEach
    void setUp() {
        agendaId = UUID.randomUUID();
        cpf = "12345678900";
        voteStatus = VoteStatus.YES;
        
        session = new Session(
            LocalDateTime.now().minusHours(1), // Started 1 hour ago
            LocalDateTime.now().plusHours(1)   // Ends in 1 hour
        );
        
        agenda = new Agenda("Test Agenda", "Test Description", session);
    }

    @Test
    @DisplayName("Should register vote successfully when all conditions are met")
    void shouldRegisterVoteSuccessfully() {
        // Given
        when(voteGateway.existsByAgendaIdAndCpf(agendaId, cpf)).thenReturn(false);

        // When
        Vote result = voteUseCase.registerVote(agendaId, agenda, cpf, voteStatus);

        // Then
        assertNotNull(result);
        assertEquals(agenda, result.getAgenda());
        assertEquals(cpf, result.getCpf());
        assertEquals(voteStatus, result.getVote());
        assertNotNull(result.getDateTime());
        
        verify(voteGateway).existsByAgendaIdAndCpf(agendaId, cpf);
        verify(voteGateway).save(any(Vote.class));
    }

    @Test
    @DisplayName("Should throw exception when agendaId is null")
    void shouldThrowExceptionWhenAgendaIdIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> voteUseCase.registerVote(null, agenda, cpf, voteStatus)
        );
        
        assertEquals("All fields must be filled.", exception.getMessage());
        verify(voteGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when cpf is null")
    void shouldThrowExceptionWhenCpfIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> voteUseCase.registerVote(agendaId, agenda, null, voteStatus)
        );
        
        assertEquals("All fields must be filled.", exception.getMessage());
        verify(voteGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when vote is null")
    void shouldThrowExceptionWhenVoteIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> voteUseCase.registerVote(agendaId, agenda, cpf, null)
        );
        
        assertEquals("All fields must be filled.", exception.getMessage());
        verify(voteGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when participant has already voted")
    void shouldThrowExceptionWhenParticipantAlreadyVoted() {
        // Given
        when(voteGateway.existsByAgendaIdAndCpf(agendaId, cpf)).thenReturn(true);

        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> voteUseCase.registerVote(agendaId, agenda, cpf, voteStatus)
        );
        
        assertEquals("Participant has already voted on this agenda.", exception.getMessage());
        verify(voteGateway).existsByAgendaIdAndCpf(agendaId, cpf);
        verify(voteGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when session has ended")
    void shouldThrowExceptionWhenSessionHasEnded() {
        // Given
        Session endedSession = new Session(
            LocalDateTime.now().minusHours(3), // Started 3 hours ago
            LocalDateTime.now().minusHours(1)  // Ended 1 hour ago
        );
        Agenda agendaWithEndedSession = new Agenda("Test", "Test", endedSession);
        
        when(voteGateway.existsByAgendaIdAndCpf(agendaId, cpf)).thenReturn(false);

        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> voteUseCase.registerVote(agendaId, agendaWithEndedSession, cpf, voteStatus)
        );
        
        assertEquals("Voting session has ended.", exception.getMessage());
        verify(voteGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when session has not started yet")
    void shouldThrowExceptionWhenSessionHasNotStarted() {
        // Given
        Session futureSession = new Session(
            LocalDateTime.now().plusHours(1), // Starts in 1 hour
            LocalDateTime.now().plusHours(3)  // Ends in 3 hours
        );
        Agenda agendaWithFutureSession = new Agenda("Test", "Test", futureSession);
        
        when(voteGateway.existsByAgendaIdAndCpf(agendaId, cpf)).thenReturn(false);

        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> voteUseCase.registerVote(agendaId, agendaWithFutureSession, cpf, voteStatus)
        );
        
        assertEquals("Voting session has not started yet.", exception.getMessage());
        verify(voteGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should return true when participant has voted")
    void shouldReturnTrueWhenParticipantHasVoted() {
        // Given
        when(voteGateway.existsByAgendaIdAndCpf(agendaId, cpf)).thenReturn(true);

        // When
        boolean result = voteUseCase.hasVoted(agendaId, cpf);

        // Then
        assertTrue(result);
        verify(voteGateway).existsByAgendaIdAndCpf(agendaId, cpf);
    }

    @Test
    @DisplayName("Should return false when participant has not voted")
    void shouldReturnFalseWhenParticipantHasNotVoted() {
        // Given
        when(voteGateway.existsByAgendaIdAndCpf(agendaId, cpf)).thenReturn(false);

        // When
        boolean result = voteUseCase.hasVoted(agendaId, cpf);

        // Then
        assertFalse(result);
        verify(voteGateway).existsByAgendaIdAndCpf(agendaId, cpf);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when CPF is invalid")
    void shouldThrowExceptionWhenCpfIsInvalid() {
        // Given
        String invalidCpf = "12345678901"; // CPF inválido

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> voteUseCase.registerVote(agendaId, agenda, invalidCpf, voteStatus)
        );

        assertEquals("Invalid CPF provided", exception.getMessage());
        verify(voteGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should allow vote when CPF is valid")
    void shouldAllowVoteWhenCpfIsValid() {
        // Given
        String validCpf = "11144477735"; // CPF válido
        when(voteGateway.existsByAgendaIdAndCpf(agendaId, validCpf)).thenReturn(false);

        // When
        Vote result = voteUseCase.registerVote(agendaId, agenda, validCpf, voteStatus);

        // Then
        assertNotNull(result);
        assertEquals(validCpf, result.getCpf());
        assertEquals(agendaId, result.getAgenda().getId());
        assertEquals(voteStatus, result.getVote());
        verify(voteGateway).save(any(Vote.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when CPF is formatted but invalid")
    void shouldThrowExceptionWhenFormattedCpfIsInvalid() {
        // Given
        String invalidFormattedCpf = "123.456.789-01"; // CPF inválido com formatação

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> voteUseCase.registerVote(agendaId, agenda, invalidFormattedCpf, voteStatus)
        );

        assertEquals("Invalid CPF provided", exception.getMessage());
        verify(voteGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should allow vote when CPF is formatted and valid")
    void shouldAllowVoteWhenFormattedCpfIsValid() {
        // Given
        String validFormattedCpf = "111.444.777-35"; // CPF válido com formatação
        when(voteGateway.existsByAgendaIdAndCpf(agendaId, validFormattedCpf)).thenReturn(false);

        // When
        Vote result = voteUseCase.registerVote(agendaId, agenda, validFormattedCpf, voteStatus);

        // Then
        assertNotNull(result);
        assertEquals(validFormattedCpf, result.getCpf());
        assertEquals(agendaId, result.getAgenda().getId());
        assertEquals(voteStatus, result.getVote());
        verify(voteGateway).save(any(Vote.class));
    }
}
