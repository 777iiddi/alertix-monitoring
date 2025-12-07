package com.alertix.alerts.repository;

import com.alertix.alerts.entity.AlertRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for AlertRule entity
 */
@Repository
public interface AlertRuleRepository extends JpaRepository<AlertRule, Long> {

    List<AlertRule> findByEnabled(Boolean enabled);

    List<AlertRule> findByHostId(Long hostId);

    List<AlertRule> findByServiceId(Long serviceId);

    @Query("SELECT ar FROM AlertRule ar WHERE ar.enabled = true")
    List<AlertRule> findAllEnabled();
}
