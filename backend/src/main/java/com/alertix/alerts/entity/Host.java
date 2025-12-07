package com.alertix.alerts.entity;

import com.alertix.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a monitored host in the system
 */
@Entity
@Table(name = "hosts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Host extends BaseEntity {
    
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    
    @Column(name = "ip_address", nullable = false)
    private String ipAddress;
    
    @Column
    private String description;
    
    @Column
    private String location;
    
    @Column
    private String environment;
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean active = true;
    
    @Override
    public String toString() {
        return "Host{" +
               "id='" + getId() + '\'' +
               ", name='" + name + '\'' +
               ", ipAddress='" + ipAddress + '\'' +
               '}';
    }
}
