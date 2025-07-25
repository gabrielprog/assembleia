package br.com.assembleia.assembleia.adapters.gateways;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.assembleia.assembleia.adapters.repositories.AgendaRepository;
import br.com.assembleia.assembleia.infra.db.entities.Agenda;

@Component
public class AgendaGatewayImpl implements AgendaGateway {
    private final AgendaRepository agendaRepository;

    public AgendaGatewayImpl(AgendaRepository agendaRepository) {
        this.agendaRepository = agendaRepository;
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=false)
    public void save(Agenda agenda) {
        agendaRepository.save(agenda);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public Optional<Agenda> findById(UUID id) {
        return agendaRepository.findById(id);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public List<Agenda> findAll() {
        return agendaRepository.findAll();
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public List<Agenda> findBySessionId(UUID sessionId) {
        return agendaRepository.findBySessionId(sessionId);
    }
}
