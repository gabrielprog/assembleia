package br.com.assembleia.assembleia.adapters.dtos;

import br.com.assembleia.assembleia.adapters.enums.VoteStatus;
import java.util.UUID;

public record VoteRequestDTO(
    UUID agendaId,
    String cpf,
    VoteStatus vote
) {
}
