package com.alertix.adapters.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "hosts")
@Data
public class JpaHostEntity {
    @Id
    private UUID id;

    private String name;
    private String ip;
    private String os;
    private String tags;

    private Instant createdAt;
    private Instant updatedAt;
}
