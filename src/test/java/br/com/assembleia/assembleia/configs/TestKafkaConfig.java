package br.com.assembleia.assembleia.configs;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "false", matchIfMissing = true)
public class TestKafkaConfig {
    // Esta configuração desabilita Kafka quando o perfil 'test' está ativo
}
