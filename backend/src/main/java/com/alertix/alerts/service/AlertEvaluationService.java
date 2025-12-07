package com.alertix.alerts.service;

import com.alertix.alerts.entity.Alert;
import com.alertix.alerts.entity.AlertRule;
import com.alertix.alerts.entity.Host;
import com.alertix.alerts.entity.MonitoredService;
import com.alertix.alerts.repository.AlertRepository;
import com.alertix.alerts.repository.AlertRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Service responsible for evaluating alert rules and triggering alerts
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertEvaluationService {

    private final AlertRuleRepository alertRuleRepository;
    private final AlertRepository alertRepository;
    private final MetricsService metricsService;
    private final NotificationService notificationService;

    /**
     * Scheduled task that runs every minute to evaluate all active alert rules
     */
    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void evaluateAllRules() {
        log.info("Starting evaluation of all active alert rules");
        List<AlertRule> activeRules = alertRuleRepository.findByEnabled(true);
        
        activeRules.forEach(rule -> {
            try {
                evaluateRule(rule);
            } catch (Exception e) {
                log.error("Error evaluating rule {}: {}", rule.getId(), e.getMessage(), e);
            }
        });
    }

    /**
     * Evaluates a single alert rule and triggers alerts if conditions are met
     * @param rule The alert rule to evaluate
     */
    @Transactional
    public void evaluateRule(AlertRule rule) {
        if (rule == null || !Boolean.TRUE.equals(rule.getEnabled())) {
            log.debug("Skipping null or disabled rule");
            return;
        }

        log.debug("Evaluating rule: {} - {}", rule.getId(), rule.getName());
        
        try {
            // Get the current metric value
            double currentValue = metricsService.getCurrentMetricValue(
                rule.getCondition(), // Using condition field as metric name
                rule.getHost(), 
                rule.getService()
            );

            // Check if the condition is met
            if (rule.getThreshold() != null) {
                boolean conditionMet = checkCondition(
                    currentValue, 
                    rule.getCondition(), 
                    rule.getThreshold()
                );

                if (conditionMet) {
                    handleTriggeredRule(rule, currentValue);
                } else {
                    log.debug("Condition not met for rule: {}", rule.getId());
                }
            } else {
                log.warn("No threshold defined for rule: {}", rule.getId());
            }
        } catch (Exception e) {
            log.error("Error evaluating rule {}: {}", rule.getId(), e.getMessage(), e);
        }
    }

    /**
     * Checks if the condition is met based on the operator and threshold
     * @param currentValue The current metric value
     * @param condition The condition string (e.g., "CPU_USAGE >")
     * @param threshold The threshold value
     * @return true if condition is met, false otherwise
     */
    private boolean checkCondition(double currentValue, String condition, double threshold) {
        if (condition == null) {
            log.warn("Condition is null");
            return false;
        }

        // Extract operator from condition (e.g., "CPU_USAGE >" -> ">")
        String operator = condition.trim().replaceAll(".*?(>|<|=|!|>=|<=|==|!=).*", "$1").trim();
        
        return switch (operator) {
            case ">" -> currentValue > threshold;
            case ">=" -> currentValue >= threshold;
            case "<" -> currentValue < threshold;
            case "<=" -> currentValue <= threshold;
            case "==" -> currentValue == threshold;
            case "!=" -> currentValue != threshold;
            default -> {
                log.warn("Unknown operator in condition '{}'", condition);
                yield false;
            }
        };
    }

    /**
     * Handles a triggered alert rule by creating an alert and sending notifications
     */
    private void handleTriggeredRule(AlertRule rule, double currentValue) {
        if (rule == null) {
            log.warn("Cannot handle null rule");
            return;
        }

        String message = String.format("Alert triggered: %s (Value: %.2f, Threshold: %.2f)", 
            rule.getName(), 
            currentValue, 
            rule.getThreshold() != null ? rule.getThreshold() : 0.0);
            
        log.warn("Rule triggered: {}", message);

        try {
            // Create and save alert
            Alert alert = Alert.builder()
                .rule(rule)
                .host(rule.getHost())
                .service(rule.getService())
                .status(Alert.AlertStatus.FIRING)
                .severity(rule.getSeverity())
                .value(currentValue)
                .message(message)
                .triggeredAt(LocalDateTime.now())
                .build();

            alertRepository.save(alert);
            notificationService.sendAlertNotification(alert);
        } catch (Exception e) {
            log.error("Error creating alert for rule {}: {}", rule.getId(), e.getMessage(), e);
        }
    }
}
