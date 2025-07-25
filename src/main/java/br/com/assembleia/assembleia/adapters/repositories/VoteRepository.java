package br.com.assembleia.assembleia.adapters.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.assembleia.assembleia.infra.db.entities.Vote;

import java.util.UUID;

public interface VoteRepository extends JpaRepository<Vote, UUID> {
    boolean existsByAgendaIdAndCpf(UUID agendaId, String cpf);
}
