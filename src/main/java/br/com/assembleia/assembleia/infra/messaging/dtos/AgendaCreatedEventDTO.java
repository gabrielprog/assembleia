package br.com.assembleia.assembleia.infra.messaging.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para evento de agenda criada
 */
public record AgendaCreatedEventDTO(
    UUID agendaId,
    String title,
    String description,
    UUID sessionId,
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt,
    
    String eventType
) {
    public static AgendaCreatedEventDTO from(UUID agendaId, String title, String description, UUID sessionId) {
        return new AgendaCreatedEventDTO(
            agendaId,
            title,
            description,
            sessionId,
            LocalDateTime.now(),
            "AGENDA_CREATED"
        );
    }
}
