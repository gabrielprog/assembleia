package br.com.assembleia.assembleia.adapters.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.assembleia.assembleia.infra.db.entities.Agenda;

import java.util.UUID;

public interface AgendaRepository extends JpaRepository<Agenda, UUID> {}
