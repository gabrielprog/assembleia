package br.com.assembleia.assembleia.infra.messaging.consumers;

import br.com.assembleia.assembleia.adapters.gateways.AgendaGateway;
import br.com.assembleia.assembleia.adapters.gateways.VoteGateway;
import br.com.assembleia.assembleia.infra.db.entities.Agenda;
import br.com.assembleia.assembleia.infra.db.entities.Vote;
import br.com.assembleia.assembleia.infra.messaging.config.KafkaTopicConfig;
import br.com.assembleia.assembleia.infra.messaging.dtos.VoteRegisteredEventDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class VoteEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(VoteEventConsumer.class);
    private final VoteGateway voteGateway;
    private final AgendaGateway agendaGateway;

    public VoteEventConsumer(VoteGateway voteGateway, AgendaGateway agendaGateway) {
        this.voteGateway = voteGateway;
        this.agendaGateway = agendaGateway;
    }

    @KafkaListener(topics = KafkaTopicConfig.VOTE_EVENTS_TOPIC, groupId = "assembleia-vote-group")
    public void consumeVoteRegisteredEvent(
            @Payload VoteRegisteredEventDTO event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        try {
            logger.info("Recebido evento de voto registrado: {} da partição {} offset {}", 
                   event.agendaId(), partition, offset);

            Agenda agenda = agendaGateway.findById(event.agendaId())
                .orElseThrow(() -> new IllegalArgumentException("Agenda não encontrada: " + event.agendaId()));

            if (voteGateway.existsByAgendaIdAndCpf(event.agendaId(), event.cpf())) {
                logger.warn("Voto duplicado ignorado para CPF {} na agenda {}", event.cpf(), event.agendaId());
                acknowledgment.acknowledge();
                return;
            }

            Vote vote = new Vote(agenda, event.cpf(), event.vote(), event.votedAt());
            
            voteGateway.save(vote);
            
            acknowledgment.acknowledge();

            logger.info("Voto registrado com sucesso para agenda {}", event.agendaId());

        } catch (Exception e) {
            logger.error("Erro ao processar evento de voto registrado: {}", e.getMessage(), e);
            throw new IllegalStateException("Falha no processamento do evento de voto: " + event.voteId(), e);
        }
    }
}
