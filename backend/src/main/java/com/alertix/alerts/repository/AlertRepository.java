package com.alertix.alerts.repository;

import com.alertix.alerts.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Alert entity
 */
@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByStatus(Alert.AlertStatus status);

    List<Alert> findBySeverity(Alert.AlertRule.AlertSeverity severity);

    @Query("SELECT a FROM Alert a WHERE a.status = 'FIRING' ORDER BY a.triggeredAt DESC")
    List<Alert> findActivesAlerts();

    @Query("SELECT a FROM Alert a WHERE a.rule.id = :ruleId AND a.status = 'FIRING'")
    List<Alert> findFiringAlertsByRule(@Param("ruleId") Long ruleId);

    @Query("SELECT a FROM Alert a WHERE a.triggeredAt >= :from ORDER BY a.triggeredAt DESC")
    List<Alert> findRecentAlerts(@Param("from") LocalDateTime from);

    @Query("SELECT COUNT(a) FROM Alert a WHERE a.status = :status")
    Long countByStatus(@Param("status") Alert.AlertStatus status);
}
