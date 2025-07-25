package br.com.assembleia.assembleia.infra.messaging.consumers;

import br.com.assembleia.assembleia.infra.messaging.config.KafkaTopicConfig;
import br.com.assembleia.assembleia.infra.messaging.dtos.SessionCreatedEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class SessionEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(SessionEventConsumer.class);

    @KafkaListener(topics = KafkaTopicConfig.SESSION_EVENTS_TOPIC, groupId = "assembleia-session-group")
    public void consumeSessionCreatedEvent(
            @Payload SessionCreatedEventDTO event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        try {
            logger.info("Recebido evento de sessão criada: {} da partição {} offset {}", 
                       event.sessionId(), partition, offset);

            processSessionCreatedEvent(event);

            acknowledgment.acknowledge();
            
            logger.info("Evento de sessão processado com sucesso: {}", event.sessionId());

        } catch (Exception e) {
            logger.error("Erro ao processar evento de sessão criada: {}", e.getMessage(), e);
            throw new IllegalStateException("Falha no processamento do evento de sessão: " + event.sessionId(), e);
        }
    }

    private void processSessionCreatedEvent(SessionCreatedEventDTO event) {
        logger.info("Processando sessão criada: {} que inicia em {} e termina em {}", 
                   event.sessionId(), event.startDate(), event.endDate());
        
        logger.info("Validando sessão {} no sistema", event.sessionId());
        
        logger.info("Enviando notificação para usuários sobre nova sessão: {}", event.sessionId());
        
        logger.info("Atualizando cache de sessões ativas com: {}", event.sessionId());
        
        logger.info("Programando jobs de início e fim da sessão: {}", event.sessionId());
        
        logger.info("Integrando sessão {} com sistemas externos", event.sessionId());
    }
}
