package br.com.assembleia.assembleia.adapters.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.assembleia.assembleia.infra.db.entities.Session;

import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> { }
