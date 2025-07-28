package br.com.assembleia.assembleia.application.usecases;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.assembleia.assembleia.adapters.dtos.VotingResultDTO;
import br.com.assembleia.assembleia.adapters.enums.VoteStatus;
import br.com.assembleia.assembleia.adapters.gateways.AgendaGateway;
import br.com.assembleia.assembleia.adapters.gateways.VoteGateway;
import br.com.assembleia.assembleia.application.utils.CpfValidator;
import br.com.assembleia.assembleia.infra.db.entities.Agenda;
import br.com.assembleia.assembleia.infra.db.entities.Vote;
import br.com.assembleia.assembleia.infra.messaging.dtos.VoteRegisteredEventDTO;
import br.com.assembleia.assembleia.infra.messaging.producers.AssembleiaEventProducer;

@Component
public class VoteUseCase {
    private final VoteGateway voteGateway;
    private final AgendaGateway agendaGateway;
    private final AssembleiaEventProducer eventProducer;

    public VoteUseCase(VoteGateway voteGateway, AgendaGateway agendaGateway, AssembleiaEventProducer eventProducer) {
        this.voteGateway = voteGateway;
        this.agendaGateway = agendaGateway;
        this.eventProducer = eventProducer;
    }

    public boolean hasVoted(UUID agendaId, String cpf) {
        return voteGateway.existsByAgendaIdAndCpf(agendaId, cpf);
    }

    public Vote registerVote(Agenda agenda, String cpf, VoteStatus vote) {

        if (agenda.getId() == null || cpf == null || vote == null) {
            throw new IllegalArgumentException("All fields must be filled.");
        }

        if (!CpfValidator.isValid(cpf)) {
            throw new IllegalArgumentException("Invalid CPF provided");
        }

        if (hasVoted(agenda.getId(), cpf)) {
            throw new IllegalStateException("Participant has already voted on this agenda.");
        }
        
        LocalDateTime currentTime = LocalDateTime.now();
        if (currentTime.isAfter(agenda.getSession().getEndDate())) {
            throw new IllegalStateException("Voting session has ended.");
        }
        
        Vote newVote = new Vote(agenda, cpf, vote, LocalDateTime.now());
        
        VoteRegisteredEventDTO event = VoteRegisteredEventDTO.from(
            newVote.getId(),
            agenda.getId(),
            cpf,
            vote,
            newVote.getDateTime()
        );
        eventProducer.publishVoteRegisteredEvent(event);

        return newVote;
    }

    public VotingResultDTO getVotingResults(UUID agendaId) {
        var agenda = agendaGateway.findById(agendaId)
            .orElseThrow(() -> new IllegalArgumentException("Agenda not found with id: " + agendaId));
        
        long yesCount = voteGateway.countByAgendaIdAndVote(agendaId, VoteStatus.YES);
        long noCount = voteGateway.countByAgendaIdAndVote(agendaId, VoteStatus.NO);
        
        boolean sessionEnded = LocalDateTime.now().isAfter(agenda.getSession().getEndDate());
        
        return VotingResultDTO.create(
            agendaId,
            agenda.getTitle(),
            yesCount,
            noCount,
            sessionEnded
        );
    }
}
