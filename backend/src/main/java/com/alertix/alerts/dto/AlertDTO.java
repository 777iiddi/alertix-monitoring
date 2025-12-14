package com.alertix.alerts.dto;

import com.alertix.alerts.entity.AlertRule.AlertSeverity;
import com.alertix.alerts.entity.AlertStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertDTO {
    private Long id;
    private Long ruleId;
    private Long hostId;
    private Long serviceId;
    private String message;
    private Double value;
    private AlertStatus status;
    private AlertSeverity severity;
    private LocalDateTime triggeredAt;
    private LocalDateTime resolvedAt;
}
