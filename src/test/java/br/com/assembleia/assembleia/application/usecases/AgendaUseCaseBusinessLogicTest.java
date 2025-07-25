package br.com.assembleia.assembleia.application.usecases;

import br.com.assembleia.assembleia.adapters.gateways.AgendaGateway;
import br.com.assembleia.assembleia.adapters.gateways.SessionGateway;
import br.com.assembleia.assembleia.infra.db.entities.Agenda;
import br.com.assembleia.assembleia.infra.db.entities.Session;
import br.com.assembleia.assembleia.infra.messaging.producers.AssembleiaEventProducer;
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
@DisplayName("AgendaUseCase Business Logic Tests")
class AgendaUseCaseBusinessLogicTest {

    @Mock
    private AgendaGateway agendaGateway;

    @Mock
    private SessionGateway sessionGateway;

    @Mock
    private AssembleiaEventProducer eventProducer;

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
    @DisplayName("Should validate agenda is not null")
    void shouldValidateAgendaIsNotNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> agendaUseCase.save(null)
        );
        
        assertEquals("Invalid agenda: cannot be null.", exception.getMessage());
        verify(agendaGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should validate agenda title is not null")
    void shouldValidateAgendaTitleIsNotNull() {
        Agenda agendaWithNullTitle = new Agenda(null, "Description", session);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> agendaUseCase.save(agendaWithNullTitle)
        );
        
        assertEquals("Agenda title is required.", exception.getMessage());
        verify(agendaGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should validate agenda title is not empty")
    void shouldValidateAgendaTitleIsNotEmpty() {
        Agenda agendaWithEmptyTitle = new Agenda("   ", "Description", session);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> agendaUseCase.save(agendaWithEmptyTitle)
        );
        
        assertEquals("Agenda title is required.", exception.getMessage());
        verify(agendaGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should validate session is not null")
    void shouldValidateSessionIsNotNull() {
        Agenda agendaWithNullSession = new Agenda("Title", "Description", null);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> agendaUseCase.save(agendaWithNullSession)
        );
        
        assertEquals("Session is required for the agenda.", exception.getMessage());
        verify(agendaGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should save valid agenda successfully")
    void shouldSaveValidAgendaSuccessfully() {
        // Act
        agendaUseCase.save(validAgenda);
        
        // Assert
        verify(agendaGateway).save(validAgenda);
        verify(eventProducer).publishAgendaCreatedEvent(any());
    }

    @Test
    @DisplayName("Should accept agenda with null description")
    void shouldAcceptAgendaWithNullDescription() {
        Agenda agendaWithNullDescription = new Agenda("Title", null, session);
        
        // Should not throw exception
        assertDoesNotThrow(() -> agendaUseCase.save(agendaWithNullDescription));
        
        verify(agendaGateway).save(agendaWithNullDescription);
    }

    @Test
    @DisplayName("Should create agenda when session exists")
    void shouldCreateAgendaWhenSessionExists() {
        // Arrange
        String title = "New Agenda";
        String description = "New Description";
        when(sessionGateway.findById(sessionId)).thenReturn(Optional.of(session));
        
        // Act
        Agenda result = agendaUseCase.createAgenda(title, description, sessionId);
        
        // Assert
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
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> agendaUseCase.createAgenda("Title", "Description", null)
        );
        
        assertEquals("Session ID is required.", exception.getMessage());
        verify(sessionGateway, never()).findById(any());
        verify(agendaGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when session not found")
    void shouldThrowExceptionWhenSessionNotFound() {
        // Arrange
        when(sessionGateway.findById(sessionId)).thenReturn(Optional.empty());
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> agendaUseCase.createAgenda("Title", "Description", sessionId)
        );
        
        assertEquals("Session not found with the provided ID.", exception.getMessage());
        verify(sessionGateway).findById(sessionId);
        verify(agendaGateway, never()).save(any());
    }
}
