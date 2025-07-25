package br.com.assembleia.assembleia.infra.messaging.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para evento de sessão criada
 */
public record SessionCreatedEventDTO(
    UUID sessionId,
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime startDate,
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime endDate,
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt,
    
    String eventType
) {
    public static SessionCreatedEventDTO from(UUID sessionId, LocalDateTime startDate, LocalDateTime endDate) {
        return new SessionCreatedEventDTO(
            sessionId,
            startDate,
            endDate,
            LocalDateTime.now(),
            "SESSION_CREATED"
        );
    }
}
