package com.alertix.alerts.entity;

import com.alertix.common.entity.BaseEntity;
import com.alertix.alerts.entity.Host;
import com.alertix.alerts.entity.MonitoredService;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Alert Entity
 *
 * Represents an alert instance when a rule is triggered
 */
@Entity
@Table(name = "alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private AlertRule rule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private Host host;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private MonitoredService service;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AlertStatus status = AlertStatus.FIRING;

    @Enumerated(EnumType.STRING)
    private AlertRule.AlertSeverity severity;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    private Double value;

    @Column(name = "triggered_at", nullable = false)
    private LocalDateTime triggeredAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;

    @Column(name = "acknowledged_by", length = 100)
    private String acknowledgedBy;

    public enum AlertStatus {
        FIRING, RESOLVED, ACKNOWLEDGED
    }
}
