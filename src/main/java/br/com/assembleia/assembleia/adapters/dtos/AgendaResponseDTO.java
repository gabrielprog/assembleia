package br.com.assembleia.assembleia.adapters.dtos;

import java.util.UUID;

public record AgendaResponseDTO(
    UUID id,
    String title,
    String description,
    UUID sessionId
) {
}
