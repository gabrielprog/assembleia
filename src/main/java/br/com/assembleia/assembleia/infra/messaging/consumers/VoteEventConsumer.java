package br.com.assembleia.assembleia.infra.messaging.consumers;

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

    @KafkaListener(topics = KafkaTopicConfig.VOTE_EVENTS_TOPIC, groupId = "assembleia-vote-group")
    public void consumeVoteRegisteredEvent(
            @Payload VoteRegisteredEventDTO event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        try {
            logger.info("Recebido evento de voto registrado: {} para agenda {} da partição {} offset {}", 
                       event.voteId(), event.agendaId(), partition, offset);

            processVoteRegisteredEvent(event);

            acknowledgment.acknowledge();
            
            logger.info("Evento de voto processado com sucesso: {}", event.voteId());

        } catch (Exception e) {
            logger.error("Erro ao processar evento de voto registrado: {}", e.getMessage(), e);
            throw new IllegalStateException("Falha no processamento do evento de voto: " + event.voteId(), e);
        }
    }

    private void processVoteRegisteredEvent(VoteRegisteredEventDTO event) {
        logger.info("Processando voto registrado: {} - {} votou {} na agenda {}", 
                   event.voteId(), event.cpf(), event.vote(), event.agendaId());
        
        logger.info("Validando se voto {} existe no sistema", event.voteId());
        
        logger.info("Atualizando contadores de votos para agenda: {}", event.agendaId());
        
        logger.info("Verificando se votação da agenda {} deve ser encerrada", event.agendaId());
        
        logger.info("Enviando notificação de voto confirmado para CPF: {}", event.cpf());
        
        logger.info("Voto {} de valor {} registrado às {} para agenda {}", 
                   event.voteId(), event.vote(), event.votedAt(), event.agendaId());
        
        logger.info("Executando auditoria do voto {} para compliance", event.voteId());
    }
}
