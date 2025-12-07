package com.alertix.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Alertix application properties.
 * Maps to 'alertix' prefix in application.yml
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "alertix")
public class AlertixProperties {

    private JwtConfig jwt = new JwtConfig();
    private SchedulerConfig scheduler = new SchedulerConfig();
    private ChecksConfig checks = new ChecksConfig();
    private AlertsConfig alerts = new AlertsConfig();
    private MetricsConfig metrics = new MetricsConfig();
    private NotificationsConfig notifications = new NotificationsConfig();
    private CorsConfig cors = new CorsConfig();
    private RateLimitConfig rateLimit = new RateLimitConfig();

    @Data
    public static class JwtConfig {
        private String secret;
        private Long accessTokenExpiration;
        private Long refreshTokenExpiration;
        private String issuer;
    }

    @Data
    public static class SchedulerConfig {
        private Boolean enabled;
        private Integer threadPoolSize;
    }

    @Data
    public static class ChecksConfig {
        private Integer defaultInterval;
        private Integer timeout;
        private Integer retries;
    }

    @Data
    public static class AlertsConfig {
        private Integer evaluationInterval;
        private Integer retentionDays;
    }

    @Data
    public static class MetricsConfig {
        private Integer retentionDays;
        private List<String> aggregationIntervals;
    }

    @Data
    public static class NotificationsConfig {
        private Integer retryAttempts;
        private Long retryDelay;
    }

    @Data
    public static class CorsConfig {
        private List<String> allowedOrigins;
        private List<String> allowedMethods;
        private List<String> allowedHeaders;
        private Boolean allowCredentials;
        private Long maxAge;
    }

    @Data
    public static class RateLimitConfig {
        private Boolean enabled;
        private Integer requestsPerMinute;
    }
}
