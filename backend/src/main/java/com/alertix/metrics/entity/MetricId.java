package com.alertix.metrics.entity;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Composite Primary Key for Metric Entity
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MetricId implements Serializable {
    private LocalDateTime time;
    private Long hostId;
    private String metricName;
}
