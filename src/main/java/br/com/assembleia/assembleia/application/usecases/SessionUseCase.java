package br.com.assembleia.assembleia.application.usecases;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import br.com.assembleia.assembleia.adapters.gateways.SessionGateway;
import br.com.assembleia.assembleia.infra.db.entities.Session;

@Component
public class SessionUseCase {
    private final SessionGateway sessionGateway;

    public SessionUseCase(SessionGateway sessionGateway) {
        this.sessionGateway = sessionGateway;
    }

    public void save(Session session) {
        if (session == null) {
            throw new IllegalArgumentException("Invalid session: cannot be null.");
        }

        if (session.getStartDate() == null) {
            throw new IllegalArgumentException("Invalid session: start date cannot be null.");
        }

        if (session.getEndDate() == null) {
            throw new IllegalArgumentException("Invalid session: end date cannot be null.");
        }

        if (session.getEndDate().isBefore(session.getStartDate())) {
            throw new IllegalArgumentException("Invalid session: end date cannot be before start date.");
        }

        if (Duration.between(session.getStartDate(), session.getEndDate()).toMinutes() < 1) {
            session.setEndDate(session.getStartDate().plusMinutes(1));
        }

        sessionGateway.save(session);
    }
}
