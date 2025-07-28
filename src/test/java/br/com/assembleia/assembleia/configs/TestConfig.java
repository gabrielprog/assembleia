package br.com.assembleia.assembleia.configs;

import br.com.assembleia.assembleia.infra.messaging.producers.AssembleiaEventProducer;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;


@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public KafkaTemplate<String, Object> mockKafkaTemplate() {
        return Mockito.mock(KafkaTemplate.class);
    }

    @Bean
    @Primary
    public AssembleiaEventProducer mockAssembleiaEventProducer() {
        return Mockito.mock(AssembleiaEventProducer.class);
    }
}
