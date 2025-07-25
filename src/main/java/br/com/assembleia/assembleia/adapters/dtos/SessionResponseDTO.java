package br.com.assembleia.assembleia.adapters.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record SessionResponseDTO(
    UUID id,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Long version
) {
}
