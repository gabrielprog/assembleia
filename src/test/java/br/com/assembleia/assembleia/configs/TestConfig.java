package br.com.assembleia.assembleia.configs;

import br.com.assembleia.assembleia.infra.messaging.producers.AssembleiaEventProducer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.mock;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    @SuppressWarnings("unchecked")
    public AssembleiaEventProducer mockEventProducer() {
        // Cria um mock do KafkaTemplate para evitar dependência real do Kafka
        KafkaTemplate<String, Object> mockKafkaTemplate = mock(KafkaTemplate.class);
        return new AssembleiaEventProducer(mockKafkaTemplate);
    }
}
