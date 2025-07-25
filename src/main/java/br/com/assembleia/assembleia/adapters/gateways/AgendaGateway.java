package br.com.assembleia.assembleia.adapters.gateways;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.assembleia.assembleia.infra.db.entities.Agenda;

@Component
public interface AgendaGateway {
    void save(Agenda agenda);
    Optional<Agenda> findById(UUID id);
}
