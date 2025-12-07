package com.alertix.alerts.service;

import com.alertix.alerts.entity.Alert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service responsible for sending notifications when alerts are triggered
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    /**
     * Sends a notification for a triggered alert
     * @param alert The alert that was triggered
     */
    public void sendAlertNotification(Alert alert) {
        // TODO: Implement actual notification logic (email, Slack, etc.)
        log.warn("ALERT TRIGGERED - {}: {} - Current Value: {}",
                alert.getRule().getName(),
                alert.getMessage(),
                alert.getValue());
        
        // TODO: Add actual notification channels here
        // - Email
        // - Slack
        // - Webhook
        // - SMS
    }
}
