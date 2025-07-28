package br.com.assembleia.assembleia.application.usecases;

import br.com.assembleia.assembleia.adapters.gateways.SessionGateway;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionUseCase Business Logic Tests")
class SessionUseCaseBusinessLogicTest {

    @Mock
    private SessionGateway sessionGateway;
    
    @Mock
    private AssembleiaEventProducer eventProducer;

    @InjectMocks
    private SessionUseCase sessionUseCase;

    private LocalDateTime now;
    private LocalDateTime futureStart;
    private LocalDateTime futureEnd;
    private LocalDateTime pastStart;
    private LocalDateTime pastEnd;
    private UUID sessionId;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        futureStart = now.plusHours(1);
        futureEnd = now.plusHours(3);
        pastStart = now.minusHours(3);
        pastEnd = now.minusHours(1);
        sessionId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should create valid session successfully")
    void shouldCreateValidSessionSuccessfully() {
        // Arrange
        Session session = new Session(futureStart, futureEnd);

        // Act
        sessionUseCase.save(session);

        // Assert
        verify(sessionGateway).save(session);
    }

    @Test
    @DisplayName("Should reject null session")
    void shouldRejectNullSession() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> sessionUseCase.save(null)
        );
        
        assertEquals("Invalid session: cannot be null.", exception.getMessage());
        verify(sessionGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should reject session with null start date")
    void shouldRejectSessionWithNullStartDate() {
        // Arrange
        Session session = new Session(null, futureEnd);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> sessionUseCase.save(session)
        );
        
        assertEquals("Invalid session: start date cannot be null.", exception.getMessage());
        verify(sessionGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should reject session with null end date")
    void shouldRejectSessionWithNullEndDate() {
        // Arrange
        Session session = new Session(futureStart, null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> sessionUseCase.save(session)
        );
        
        assertEquals("Invalid session: end date cannot be null.", exception.getMessage());
        verify(sessionGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should reject session with end date before start date")
    void shouldRejectSessionWithEndDateBeforeStartDate() {
        // Arrange
        Session session = new Session(futureEnd, futureStart); // end before start

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> sessionUseCase.save(session)
        );
        
        assertEquals("Invalid session: end date cannot be before start date.", exception.getMessage());
        verify(sessionGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should auto-adjust session duration when less than 1 minute")
    void shouldAutoAdjustSessionDurationWhenLessThanOneMinute() {
        // Arrange - Very short session (less than 1 minute)
        LocalDateTime start = futureStart;
        LocalDateTime end = start.plusSeconds(30);
        Session shortSession = new Session(start, end);

        // Act
        sessionUseCase.save(shortSession);

        // Assert
        verify(sessionGateway).save(argThat(session -> 
            session.getEndDate().equals(start.plusMinutes(1))
        ));
    }

    @Test
    @DisplayName("Should save session with valid duration")
    void shouldSaveSessionWithValidDuration() {
        // Arrange - Session with 2 hours duration
        Session session = new Session(futureStart, futureEnd);

        // Act
        sessionUseCase.save(session);

        // Assert
        verify(sessionGateway).save(session);
        assertEquals(futureStart, session.getStartDate());
        assertEquals(futureEnd, session.getEndDate());
    }

    @Test
    @DisplayName("Should save session with past start date") 
    void shouldSaveSessionWithPastStartDate() {
        // Arrange - A implementação atual não valida data passada
        Session session = new Session(pastStart, futureEnd);

        // Act
        sessionUseCase.save(session);

        // Assert
        verify(sessionGateway).save(session);
    }

    @Test
    @DisplayName("Should save session with very long duration")
    void shouldSaveSessionWithVeryLongDuration() {
        // Arrange - A implementação atual não limita duração máxima
        LocalDateTime start = futureStart;
        LocalDateTime end = start.plusDays(2); // 48 horas
        Session longSession = new Session(start, end);

        // Act
        sessionUseCase.save(longSession);

        // Assert
        verify(sessionGateway).save(longSession);
    }

    @Test
    @DisplayName("Should handle session with same start and end date")
    void shouldHandleSessionWithSameStartAndEndDate() {
        // Arrange
        Session session = new Session(futureStart, futureStart);

        // Act
        sessionUseCase.save(session);

        // Assert - Deve ajustar para 1 minuto de duração
        verify(sessionGateway).save(argThat(s -> 
            s.getEndDate().equals(futureStart.plusMinutes(1))
        ));
    }

    @Test
    @DisplayName("Should handle valid session boundary")
    void shouldHandleValidSessionBoundary() {
        // Arrange - Sessão com exatamente 1 minuto
        LocalDateTime start = futureStart;
        LocalDateTime end = start.plusMinutes(1);
        Session session = new Session(start, end);

        // Act
        sessionUseCase.save(session);

        // Assert - Não deve alterar a duração
        verify(sessionGateway).save(session);
        assertEquals(end, session.getEndDate());
    }
}