package br.com.assembleia.assembleia.application.usecases;

import br.com.assembleia.assembleia.adapters.gateways.AgendaGateway;
import br.com.assembleia.assembleia.adapters.gateways.SessionGateway;
import br.com.assembleia.assembleia.infra.db.entities.Agenda;
import br.com.assembleia.assembleia.infra.db.entities.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AgendaUseCase Unit Tests")
class AgendaUseCaseTest {

    @Mock
    private AgendaGateway agendaGateway;

    @Mock
    private SessionGateway sessionGateway;

    @InjectMocks
    private AgendaUseCase agendaUseCase;

    private UUID sessionId;
    private Session session;
    private Agenda validAgenda;

    @BeforeEach
    void setUp() {
        sessionId = UUID.randomUUID();
        session = new Session(LocalDateTime.now(), LocalDateTime.now().plusHours(2));
        validAgenda = new Agenda("Valid Title", "Valid Description", session);
    }

    @Test
    @DisplayName("Should save agenda successfully when all conditions are met")
    void shouldSaveAgendaSuccessfullyWhenAllConditionsAreMet() {
        // When
        agendaUseCase.save(validAgenda);

        // Then
        verify(agendaGateway).save(validAgenda);
    }

    @Test
    @DisplayName("Should throw exception when agenda is null")
    void shouldThrowExceptionWhenAgendaIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> agendaUseCase.save(null)
        );

        assertEquals("Invalid agenda: cannot be null.", exception.getMessage());
        verify(agendaGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when title is null")
    void shouldThrowExceptionWhenTitleIsNull() {
        // Given
        Agenda agendaWithNullTitle = new Agenda(null, "Description", session);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> agendaUseCase.save(agendaWithNullTitle)
        );

        assertEquals("Agenda title is required.", exception.getMessage());
        verify(agendaGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when title is empty")
    void shouldThrowExceptionWhenTitleIsEmpty() {
        // Given
        Agenda agendaWithEmptyTitle = new Agenda("", "Description", session);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> agendaUseCase.save(agendaWithEmptyTitle)
        );

        assertEquals("Agenda title is required.", exception.getMessage());
        verify(agendaGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when title is only whitespace")
    void shouldThrowExceptionWhenTitleIsOnlyWhitespace() {
        // Given
        Agenda agendaWithWhitespaceTitle = new Agenda("   ", "Description", session);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> agendaUseCase.save(agendaWithWhitespaceTitle)
        );

        assertEquals("Agenda title is required.", exception.getMessage());
        verify(agendaGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when session is null")
    void shouldThrowExceptionWhenSessionIsNull() {
        // Given
        Agenda agendaWithNullSession = new Agenda("Title", "Description", null);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> agendaUseCase.save(agendaWithNullSession)
        );

        assertEquals("Session is required for the agenda.", exception.getMessage());
        verify(agendaGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should create agenda successfully when session exists")
    void shouldCreateAgendaSuccessfullyWhenSessionExists() {
        // Given
        String title = "New Agenda";
        String description = "New Description";
        when(sessionGateway.findById(sessionId)).thenReturn(Optional.of(session));

        // When
        Agenda result = agendaUseCase.createAgenda(title, description, sessionId);

        // Then
        assertNotNull(result);
        assertEquals(title, result.getTitle());
        assertEquals(description, result.getDescription());
        assertEquals(session, result.getSession());

        verify(sessionGateway).findById(sessionId);
        verify(agendaGateway).save(any(Agenda.class));
    }

    @Test
    @DisplayName("Should throw exception when sessionId is null")
    void shouldThrowExceptionWhenSessionIdIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> agendaUseCase.createAgenda("Title", "Description", null)
        );

        assertEquals("Session ID is required.", exception.getMessage());
        verify(sessionGateway, never()).findById(any());
        verify(agendaGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when session does not exist")
    void shouldThrowExceptionWhenSessionDoesNotExist() {
        // Given
        when(sessionGateway.findById(sessionId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> agendaUseCase.createAgenda("Title", "Description", sessionId)
        );

        assertEquals("Session not found with the provided ID.", exception.getMessage());
        verify(sessionGateway).findById(sessionId);
        verify(agendaGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should accept agenda with null description")
    void shouldAcceptAgendaWithNullDescription() {
        // Given
        Agenda agendaWithNullDescription = new Agenda("Title", null, session);

        // When
        agendaUseCase.save(agendaWithNullDescription);

        // Then
        verify(agendaGateway).save(agendaWithNullDescription);
    }
}
