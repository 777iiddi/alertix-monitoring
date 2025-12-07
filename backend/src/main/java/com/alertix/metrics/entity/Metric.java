package com.alertix.metrics.entity;

import com.alertix.inventory.entity.Host;
import com.alertix.inventory.entity.MonitoredService;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Metric Entity
 *
 * Represents a time-series metric data point
 */
@Entity
@Table(name = "metrics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(MetricId.class)
public class Metric {

    @Id
    @Column(nullable = false)
    private LocalDateTime time;

    @Id
    @Column(name = "host_id", nullable = false)
    private Long hostId;

    @Id
    @Column(name = "metric_name", nullable = false, length = 100)
    private String metricName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", insertable = false, updatable = false)
    private Host host;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private MonitoredService service;

    private Double value;

    private String unit;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> tags;
}
