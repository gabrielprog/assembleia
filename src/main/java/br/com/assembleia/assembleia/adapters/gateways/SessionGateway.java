package br.com.assembleia.assembleia.adapters.gateways;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.assembleia.assembleia.infra.db.entities.Session;

@Component
public interface SessionGateway {
    void save(Session session);
    Optional<Session> findById(UUID id);
}
