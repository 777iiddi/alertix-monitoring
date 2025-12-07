package com.alertix.adapters.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SpringDataHostRepository extends JpaRepository<JpaHostEntity, UUID> {
}
