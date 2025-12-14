package com.alertix.alerts.dto;

import com.alertix.alerts.entity.AlertRule.AlertSeverity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertRuleDTO {
    private Long id;
    private String name;
    private Long hostId;
    private Long serviceId;
    private String condition;
    private Double threshold;
    private Integer duration;
    private AlertSeverity severity;
    private Boolean enabled;
    private String description;
}
