package br.com.assembleia.assembleia.application.usecases;

import br.com.assembleia.assembleia.adapters.gateways.AgendaGateway;
import br.com.assembleia.assembleia.adapters.gateways.SessionGateway;
import br.com.assembleia.assembleia.infra.db.entities.Agenda;
import br.com.assembleia.assembleia.infra.db.entities.Session;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AgendaUseCaseBusinessLogicTest {
    private final AgendaGateway agendaGateway;
    private final SessionGateway sessionGateway;

    public AgendaUseCaseBusinessLogicTest(AgendaGateway agendaGateway, SessionGateway sessionGateway) {
        this.agendaGateway = agendaGateway;
        this.sessionGateway = sessionGateway;
    }

    public Void create(String title, String description, UUID sessionId) {
        validateInputs(title, description, sessionId);
        
        Session session = sessionGateway.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + sessionId));
        
        validateSession(session);
        
        Agenda agenda = new Agenda(title, description, session);
        agendaGateway.save(agenda);
        return null;
    }

    public Optional<Agenda> findById(UUID id) {
        return agendaGateway.findById(id);
    }

    public List<Agenda> findAll() {
        return agendaGateway.findAll();
    }

    public List<Agenda> findBySessionId(UUID sessionId) {
        return agendaGateway.findBySessionId(sessionId);
    }

    public boolean isVotingOpen(Agenda agenda) {
        LocalDateTime now = LocalDateTime.now();
        Session session = agenda.getSession();
        return now.isAfter(session.getStartDate()) && now.isBefore(session.getEndDate());
    }

    private void validateInputs(String title, String description, UUID sessionId) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }

        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required");
        }

        if (sessionId == null) {
            throw new IllegalArgumentException("Session ID is required");
        }

        if (title.length() > 255) {
            throw new IllegalArgumentException("Title cannot exceed 255 characters");
        }

        if (description.length() > 1000) {
            throw new IllegalArgumentException("Description cannot exceed 1000 characters");
        }
    }

    private void validateSession(Session session) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(session.getEndDate())) {
            throw new IllegalArgumentException("Cannot create agenda for expired session");
        }
    }
}