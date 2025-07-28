package br.com.assembleia.assembleia.infra.messaging.dtos;

import br.com.assembleia.assembleia.adapters.enums.VoteStatus;
import br.com.assembleia.assembleia.infra.db.entities.Vote;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public record VoteRegisteredEventDTO(
    UUID voteId,
    UUID agendaId,
    String cpf,
    VoteStatus vote,
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime votedAt,
    
    String eventType
) {
    public static VoteRegisteredEventDTO from(UUID voteId, UUID agendaId, String cpf, VoteStatus vote, LocalDateTime votedAt) {
        return new VoteRegisteredEventDTO(
            voteId,
            agendaId,
            cpf,
            vote,
            votedAt,
            "VOTE_REGISTERED"
        );
    }
}
