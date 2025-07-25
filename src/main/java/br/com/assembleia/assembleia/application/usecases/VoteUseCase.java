package br.com.assembleia.assembleia.application.usecases;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.assembleia.assembleia.adapters.enums.VoteStatus;
import br.com.assembleia.assembleia.adapters.gateways.VoteGateway;
import br.com.assembleia.assembleia.infra.db.entities.Agenda;
import br.com.assembleia.assembleia.infra.db.entities.Vote;

@Component
public class VoteUseCase {
    private final VoteGateway voteGateway;

    public VoteUseCase(VoteGateway voteGateway) {
        this.voteGateway = voteGateway;
    }

    public boolean hasVoted(UUID agendaId, String cpf) {
        return voteGateway.existsByAgendaIdAndCpf(agendaId, cpf);
    }

    public void registerVote(UUID agendaId, Agenda agenda, String cpf, VoteStatus vote) {

        if (agendaId == null || cpf == null || vote == null) {
            throw new IllegalArgumentException("All fields must be filled.");
        }

        if (hasVoted(agendaId, cpf)) {
            throw new IllegalStateException("Participant has already voted on this agenda.");
        }
        voteGateway.save(new Vote(agenda, cpf, vote, LocalDateTime.now()));
    }
}
