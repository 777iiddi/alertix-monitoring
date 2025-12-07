package com.alertix.config;

import lombok.Data;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka Configuration
 *
 * Defines Kafka topics used by the application
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaConfig {

    private TopicsConfig topics = new TopicsConfig();
    private PartitionsConfig partitions = new PartitionsConfig();
    private Integer replicationFactor;

    @Data
    public static class TopicsConfig {
        private String metricsRaw;
        private String metricsAgg;
        private String alerts;
        private String notifications;
    }

    @Data
    public static class PartitionsConfig {
        private Integer metricsRaw;
        private Integer metricsAgg;
        private Integer alerts;
        private Integer notifications;
    }

    @Bean
    public NewTopic metricsRawTopic() {
        return TopicBuilder.name(topics.getMetricsRaw())
                .partitions(partitions.getMetricsRaw())
                .replicas(replicationFactor)
                .build();
    }

    @Bean
    public NewTopic metricsAggTopic() {
        return TopicBuilder.name(topics.getMetricsAgg())
                .partitions(partitions.getMetricsAgg())
                .replicas(replicationFactor)
                .build();
    }

    @Bean
    public NewTopic alertsTopic() {
        return TopicBuilder.name(topics.getAlerts())
                .partitions(partitions.getAlerts())
                .replicas(replicationFactor)
                .build();
    }

    @Bean
    public NewTopic notificationsTopic() {
        return TopicBuilder.name(topics.getNotifications())
                .partitions(partitions.getNotifications())
                .replicas(replicationFactor)
                .build();
    }
}
