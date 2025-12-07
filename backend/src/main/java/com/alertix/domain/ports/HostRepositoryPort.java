package com.alertix.domain.ports;

import com.alertix.domain.model.Host;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HostRepositoryPort {
    Host save(Host host);
    Optional<Host> findById(UUID id);
    List<Host> findAll();
}
