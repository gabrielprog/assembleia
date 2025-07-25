package br.com.assembleia.assembleia.adapters.repositories;

import br.com.assembleia.assembleia.external.db.entities.Votos;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface VotoRepository extends JpaRepository<Votos, UUID> {
    boolean existsByPautaIdAndParticipanteId(UUID pautaId, String participanteId);
}
