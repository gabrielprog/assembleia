package br.com.assembleia.assembleia.adapters.gateways;

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
    
    @Transactional(propagation=Propagation.REQUIRED, readOnly=false)
    public Optional<Session> findById(UUID id) {
        return sessionRepository.findById(id);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW, readOnly=false)
    public void save(Session session) {
        sessionRepository.save(session);
    }
}
