package br.com.assembleia.assembleia.adapters.dtos;

import java.util.UUID;

public record AgendaRequestDTO(
    String title,
    String description,
    UUID sessionId
) {
}
