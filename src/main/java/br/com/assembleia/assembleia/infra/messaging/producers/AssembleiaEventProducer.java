package br.com.assembleia.assembleia.infra.messaging.producers;

import br.com.assembleia.assembleia.infra.db.entities.Vote;
import br.com.assembleia.assembleia.infra.messaging.config.KafkaTopicConfig;
import br.com.assembleia.assembleia.infra.messaging.dtos.VoteRegisteredEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Serviço para publicar eventos no Kafka
 */
@Service
public class AssembleiaEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(AssembleiaEventProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AssembleiaEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    /**
     * Publica evento de voto registrado
     */
    public void publishVoteRegisteredEvent(VoteRegisteredEventDTO event) {
        try {
            logger.info("Publicando evento de voto registrado: {} para agenda {}", event.voteId(), event.agendaId());

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                KafkaTopicConfig.VOTE_EVENTS_TOPIC,
                event.agendaId().toString(),
                event
            );

            future.whenComplete((result, exception) -> {
                if (exception != null) {
                    logger.error("Erro ao publicar evento de voto registrado: {}", exception.getMessage(), exception);
                } else {
                    logger.info("Evento de voto registrado publicado com sucesso: {}", result.getRecordMetadata());
                }
            });
        } catch (Exception e) {
            logger.error("Erro inesperado ao publicar evento de voto registrado: {}", e.getMessage(), e);
        }
    }
}
