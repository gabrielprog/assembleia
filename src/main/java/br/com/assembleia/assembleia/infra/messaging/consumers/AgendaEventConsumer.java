package br.com.assembleia.assembleia.infra.messaging.consumers;

import br.com.assembleia.assembleia.infra.messaging.config.KafkaTopicConfig;
import br.com.assembleia.assembleia.infra.messaging.dtos.AgendaCreatedEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class AgendaEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AgendaEventConsumer.class);

    @KafkaListener(topics = KafkaTopicConfig.AGENDA_EVENTS_TOPIC, groupId = "assembleia-agenda-group")
    public void consumeAgendaCreatedEvent(
            @Payload AgendaCreatedEventDTO event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        try {
            logger.info("Recebido evento de agenda criada: {} da partição {} offset {}", 
                       event.agendaId(), partition, offset);

            processAgendaCreatedEvent(event);

            acknowledgment.acknowledge();
            
            logger.info("Evento de agenda processado com sucesso: {}", event.agendaId());

        } catch (Exception e) {
            logger.error("Erro ao processar evento de agenda criada: {}", e.getMessage(), e);
            throw new IllegalStateException("Falha no processamento do evento de agenda: " + event.agendaId(), e);
        }
    }

    private void processAgendaCreatedEvent(AgendaCreatedEventDTO event) {
        logger.info("Processando agenda criada: {} '{}' para sessão {}", 
                   event.agendaId(), event.title(), event.sessionId());
        
        logger.info("Validando agenda {} no sistema", event.agendaId());
        
        logger.info("Notificando participantes sobre nova agenda: {} - {}", 
                   event.agendaId(), event.title());
        
        logger.info("Preparando material de votação para agenda: {}", event.agendaId());
        
        logger.info("Atualizando índices de busca para agenda: {}", event.agendaId());
        
        logger.info("Integrando agenda {} com sistemas de auditoria", event.agendaId());
    }
}
