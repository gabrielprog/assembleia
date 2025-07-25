package br.com.assembleia.assembleia.application.usecases;

import br.com.assembleia.assembleia.adapters.gateways.SessionGateway;
import br.com.assembleia.assembleia.infra.db.entities.Session;
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
@DisplayName("SessionUseCase Unit Tests")
class SessionUseCaseTest {

    @Mock
    private SessionGateway sessionGateway;

    @InjectMocks
    private SessionUseCase sessionUseCase;

    private Session validSession;
    private Session shortSession;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        validSession = new Session(now, now.plusHours(2));
        shortSession = new Session(now, now.plusSeconds(30)); // Less than 1 minute
    }

    @Test
    @DisplayName("Should save session successfully when duration is valid")
    void shouldSaveSessionSuccessfullyWhenDurationIsValid() {
        // When
        sessionUseCase.save(validSession);

        // Then
        verify(sessionGateway).save(validSession);
    }

    @Test
    @DisplayName("Should adjust end date when session duration is less than 1 minute")
    void shouldAdjustEndDateWhenSessionDurationIsLessThanOneMinute() {
        // When
        sessionUseCase.save(shortSession);

        // Then
        assertTrue(shortSession.getEndDate().isAfter(shortSession.getStartDate().plusMinutes(1).minusSeconds(1)));
        verify(sessionGateway).save(shortSession);
    }

    @Test
    @DisplayName("Should throw exception when session is null")
    void shouldThrowExceptionWhenSessionIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> sessionUseCase.save(null)
        );

        assertEquals("Invalid session: cannot be null.", exception.getMessage());
        verify(sessionGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should save session with exactly 1 minute duration")
    void shouldSaveSessionWithExactlyOneMinuteDuration() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Session oneMinuteSession = new Session(now, now.plusMinutes(1));

        // When
        sessionUseCase.save(oneMinuteSession);

        // Then
        assertEquals(now.plusMinutes(1), oneMinuteSession.getEndDate());
        verify(sessionGateway).save(oneMinuteSession);
    }
}
