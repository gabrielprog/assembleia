package br.com.assembleia.assembleia.adapters.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.assembleia.assembleia.adapters.enums.VoteStatus;
import br.com.assembleia.assembleia.infra.db.entities.Vote;

import java.util.List;
import java.util.UUID;

public interface VoteRepository extends JpaRepository<Vote, UUID> {
    boolean existsByAgendaIdAndCpf(UUID agendaId, String cpf);
    long countByAgendaIdAndVote(UUID agendaId, VoteStatus vote);
    long countByAgendaId(UUID agendaId);
    List<Vote> findByAgendaId(UUID agendaId);
}
