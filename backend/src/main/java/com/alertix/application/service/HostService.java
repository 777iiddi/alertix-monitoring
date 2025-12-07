package com.alertix.application.service;

import com.alertix.domain.model.Host;
import com.alertix.domain.ports.HostRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class HostService {

    private final HostRepositoryPort hostRepo;

    public HostService(HostRepositoryPort hostRepo) {
        this.hostRepo = hostRepo;
    }

    public Host createHost(Host h) {
        h.setId(UUID.randomUUID());
        h.setCreatedAt(Instant.now());
        return hostRepo.save(h);
    }

    public List<Host> list() {
        return hostRepo.findAll();
    }
}
