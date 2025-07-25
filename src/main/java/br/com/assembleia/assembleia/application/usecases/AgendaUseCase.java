package br.com.assembleia.assembleia.application.usecases;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.assembleia.assembleia.adapters.gateways.AgendaGateway;
import br.com.assembleia.assembleia.adapters.gateways.SessionGateway;
import br.com.assembleia.assembleia.infra.db.entities.Agenda;
import br.com.assembleia.assembleia.infra.db.entities.Session;

@Component
public class AgendaUseCase {
    private final AgendaGateway agendaGateway;
    private final SessionGateway sessionGateway;

    public AgendaUseCase(AgendaGateway agendaGateway, SessionGateway sessionGateway) {
        this.agendaGateway = agendaGateway;
        this.sessionGateway = sessionGateway;
    }

    public void save(Agenda agenda) {
        if (agenda == null) {
            throw new IllegalArgumentException("Invalid agenda: cannot be null.");
        }

        if (agenda.getTitle() == null || agenda.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Agenda title is required.");
        }

        if (agenda.getSession() == null) {
            throw new IllegalArgumentException("Session is required for the agenda.");
        }

        agendaGateway.save(agenda);
    }

    public Agenda createAgenda(String title, String description, UUID sessionId) {
        if (sessionId == null) {
            throw new IllegalArgumentException("Session ID is required.");
        }

        Optional<Session> sessionOpt = sessionGateway.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            throw new IllegalArgumentException("Session not found with the provided ID.");
        }

        Agenda agenda = new Agenda(title, description, sessionOpt.get());
        save(agenda);
        return agenda;
    }
}
