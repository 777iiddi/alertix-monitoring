package com.alertix.adapters.persistence;

import com.alertix.domain.model.Host;
import com.alertix.domain.ports.HostRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class HostRepositoryAdapter implements HostRepositoryPort {

    private final SpringDataHostRepository repo;

    public HostRepositoryAdapter(SpringDataHostRepository repo) {
        this.repo = repo;
    }

    @Override
    public Host save(Host host) {
        JpaHostEntity entity = toEntity(host);
        entity = repo.save(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<Host> findById(UUID id) {
        return repo.findById(id).map(this::toDomain);
    }

    @Override
    public List<Host> findAll() {
        return repo.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private JpaHostEntity toEntity(Host h) {
        JpaHostEntity e = new JpaHostEntity();
        e.setId(h.getId());
        e.setName(h.getName());
        e.setIp(h.getIp());
        e.setOs(h.getOs());
        e.setTags(h.getTags() == null ? null : String.join(",", h.getTags()));
        e.setCreatedAt(h.getCreatedAt());
        e.setUpdatedAt(h.getUpdatedAt());
        return e;
    }

    private Host toDomain(JpaHostEntity e) {
        Host h = new Host();
        h.setId(e.getId());
        h.setName(e.getName());
        h.setIp(e.getIp());
        h.setOs(e.getOs());
        h.setTags(e.getTags() == null ? null : Arrays.asList(e.getTags().split(",")));
        h.setCreatedAt(e.getCreatedAt());
        h.setUpdatedAt(e.getUpdatedAt());
        return h;
    }
}
