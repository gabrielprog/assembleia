package br.com.assembleia.assembleia.adapters.gateways;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.assembleia.assembleia.adapters.repositories.SessionRepository;
import br.com.assembleia.assembleia.infra.db.entities.Session;

@Component
public class SessionGatewayImpl implements SessionGateway {
    
    private final SessionRepository sessionRepository;
    
    public SessionGatewayImpl(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }
    
    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public Optional<Session> findById(UUID id) {
        return sessionRepository.findById(id);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW, readOnly=false)
    public void save(Session session) {
        sessionRepository.save(session);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public List<Session> findAll() {
        return sessionRepository.findAll();
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public List<Session> findActiveSessions() {
        LocalDateTime now = LocalDateTime.now();
        return sessionRepository.findActiveSessions(now);
    }
}
