package br.com.assembleia.assembleia.adapters.gateways;

import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.assembleia.assembleia.infra.db.entities.Vote;

@Component
public interface VoteGateway {
    boolean existsByAgendaIdAndCpf(UUID agendaId, String cpf);
    void save(Vote vote);
}
