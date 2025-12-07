package com.alertix.alerts.entity;

import com.alertix.common.entity.BaseEntity;
import com.alertix.inventory.entity.Host;
import com.alertix.inventory.entity.MonitoredService;
import jakarta.persistence.*;
import lombok.*;

/**
 * Alert Rule Entity
 *
 * Defines conditions for triggering alerts
 */
@Entity
@Table(name = "alert_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertRule extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private Host host;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private MonitoredService service;

    @Column(nullable = false, length = 500)
    private String condition;

    private Double threshold;

    @Builder.Default
    private Integer duration = 60; // seconds

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AlertSeverity severity = AlertSeverity.WARNING;

    @Builder.Default
    private Boolean enabled = true;

    @Column(columnDefinition = "TEXT")
    private String description;

    public enum AlertSeverity {
        INFO, WARNING, CRITICAL
    }
}
