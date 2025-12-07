package com.alertix.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class Host {
    private UUID id;
    private String name;
    private String ip;
    private String os;
    private List<String> tags;
    private Instant createdAt;
    private Instant updatedAt;
}
