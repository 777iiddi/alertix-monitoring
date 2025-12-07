package com.alertix.alerts.entity;

import com.alertix.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a monitored service in the system
 */
@Entity
@Table(name = "monitored_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonitoredService extends BaseEntity {
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "check_interval")
    private Integer checkInterval;
    
    @Column
    private Integer timeout;
    
    @Column
    @Builder.Default
    private Boolean active = true;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", referencedColumnName = "id")
    private Host host;
    
    @Override
    public String toString() {
        return "MonitoredService{" +
               "id='" + getId() + '\'' +
               ", name='" + name + '\'' +
               ", host=" + (host != null ? host.getName() : "none") +
               '}';
    }
}
