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
    private Session validSession;
    private Session shortSession;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        validSession = new Session(now, now.plusHours(2));
        shortSession = new Session(now, now.plusSeconds(30)); // Less than 1 minute
    }

    @Test
    @DisplayName("Should validate session is not null")
    void shouldValidateSessionIsNotNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> sessionUseCase.save(null)
        );
        
        assertEquals("Invalid session: cannot be null.", exception.getMessage());
        verify(sessionGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should validate start date is not null")
    void shouldValidateStartDateIsNotNull() {
        Session sessionWithNullStart = new Session(null, now.plusHours(1));
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> sessionUseCase.save(sessionWithNullStart)
        );
        
        assertEquals("Invalid session: start date cannot be null.", exception.getMessage());
        verify(sessionGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should validate end date is not null")
    void shouldValidateEndDateIsNotNull() {
        Session sessionWithNullEnd = new Session(now, null);
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> sessionUseCase.save(sessionWithNullEnd)
        );
        
        assertEquals("Invalid session: end date cannot be null.", exception.getMessage());
        verify(sessionGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should validate end date is not before start date")
    void shouldValidateEndDateIsNotBeforeStartDate() {
        Session invalidSession = new Session(now, now.minusHours(1)); // End before start
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> sessionUseCase.save(invalidSession)
        );
        
        assertEquals("Invalid session: end date cannot be before start date.", exception.getMessage());
        verify(sessionGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should save valid session successfully")
    void shouldSaveValidSessionSuccessfully() {
        // Act
        sessionUseCase.save(validSession);
        
        // Assert
        verify(sessionGateway).save(validSession);
        verify(eventProducer).publishSessionCreatedEvent(any());
    }

    @Test
    @DisplayName("Should adjust end date when duration is less than 1 minute")
    void shouldAdjustEndDateWhenDurationIsLessThanOneMinute() {
        // Arrange
        LocalDateTime originalEndDate = shortSession.getEndDate();
        
        // Act
        sessionUseCase.save(shortSession);
        
        // Assert
        assertTrue(shortSession.getEndDate().isAfter(originalEndDate));
        assertTrue(shortSession.getEndDate().isAfter(shortSession.getStartDate().plusMinutes(1).minusSeconds(1)));
        verify(sessionGateway).save(shortSession);
        verify(eventProducer).publishSessionCreatedEvent(any());
    }

    @Test
    @DisplayName("Should save session with exactly 1 minute duration")
    void shouldSaveSessionWithExactlyOneMinuteDuration() {
        // Arrange
        Session oneMinuteSession = new Session(now, now.plusMinutes(1));
        
        // Act
        sessionUseCase.save(oneMinuteSession);
        
        // Assert
        assertEquals(now.plusMinutes(1), oneMinuteSession.getEndDate());
        verify(sessionGateway).save(oneMinuteSession);
        verify(eventProducer).publishSessionCreatedEvent(any());
    }

    @Test
    @DisplayName("Should not adjust end date when duration is more than 1 minute")
    void shouldNotAdjustEndDateWhenDurationIsMoreThanOneMinute() {
        // Arrange
        LocalDateTime originalEndDate = validSession.getEndDate();
        
        // Act
        sessionUseCase.save(validSession);
        
        // Assert
        assertEquals(originalEndDate, validSession.getEndDate());
        verify(sessionGateway).save(validSession);
    }
}
