package br.com.assembleia.assembleia.adapters.repositories;

import br.com.assembleia.assembleia.external.db.entities.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PautaRepository extends JpaRepository<Pauta, UUID> {}