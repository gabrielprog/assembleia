package br.com.assembleia.assembleia.adapters.gateways;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.assembleia.assembleia.adapters.repositories.VoteRepository;
import br.com.assembleia.assembleia.infra.db.entities.Vote;

@Component
public class VoteGatewayImpl implements VoteGateway {
    private final VoteRepository voteRepository;

    public VoteGatewayImpl(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=false)
    public boolean existsByAgendaIdAndCpf(UUID agendaId, String cpf) {
        return voteRepository.existsByAgendaIdAndCpf(agendaId, cpf);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=false)
    public void save(Vote vote) {
        voteRepository.save(vote);
    }
}
