package br.com.assembleia.assembleia.adapters.repositories;

import br.com.assembleia.assembleia.external.db.entities.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SessaoRepository extends JpaRepository<Sessao, UUID> { }

