package br.com.assembleia.assembleia.infra.messaging.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String SESSION_EVENTS_TOPIC = "session-events";
    public static final String AGENDA_EVENTS_TOPIC = "agenda-events";
    public static final String VOTE_EVENTS_TOPIC = "vote-events";
    public static final String VOTING_RESULTS_TOPIC = "voting-results";

    @Bean
    public NewTopic sessionEventsTopic() {
        return TopicBuilder.name(SESSION_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic agendaEventsTopic() {
        return TopicBuilder.name(AGENDA_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic voteEventsTopic() {
        return TopicBuilder.name(VOTE_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic votingResultsTopic() {
        return TopicBuilder.name(VOTING_RESULTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
