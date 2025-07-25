package br.com.assembleia.assembleia.adapters.dtos;

import java.time.LocalDateTime;

public record SessionRequestDTO(
    LocalDateTime startDate,
    LocalDateTime endDate) {
}
