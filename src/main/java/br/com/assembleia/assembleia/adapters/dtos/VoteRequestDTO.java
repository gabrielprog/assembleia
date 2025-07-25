package br.com.assembleia.assembleia.adapters.dtos;

import br.com.assembleia.assembleia.adapters.enums.VoteStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Dados para registrar um voto")
public record VoteRequestDTO(
    @Schema(description = "ID da agenda", example = "550e8400-e29b-41d4-a716-446655440000")
    UUID agendaId,
    
    @Schema(description = "CPF do participante (somente números)", example = "12345678901")
    String cpf,
    
    @Schema(description = "Voto do participante", allowableValues = {"YES", "NO"})
    VoteStatus vote
) {
}
