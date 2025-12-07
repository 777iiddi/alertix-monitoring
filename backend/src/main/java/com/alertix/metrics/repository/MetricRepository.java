package com.alertix.metrics.repository;

import com.alertix.metrics.entity.Metric;
import com.alertix.metrics.entity.MetricId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Metric entity
 */
@Repository
public interface MetricRepository extends JpaRepository<Metric, MetricId> {

    @Query("SELECT m FROM Metric m WHERE m.hostId = :hostId AND m.metricName = :metricName " +
            "AND m.time >= :from AND m.time <= :to ORDER BY m.time ASC")
    List<Metric> findMetrics(
            @Param("hostId") Long hostId,
            @Param("metricName") String metricName,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query("SELECT m FROM Metric m WHERE m.hostId = :hostId AND m.time >= :from AND m.time <= :to " +
            "ORDER BY m.time ASC")
    List<Metric> findHostMetrics(
            @Param("hostId") Long hostId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query("SELECT m FROM Metric m WHERE m.service.id = :serviceId AND m.time >= :from " +
            "ORDER BY m.time DESC")
    List<Metric> findServiceMetrics(
            @Param("serviceId") Long serviceId,
            @Param("from") LocalDateTime from);

    @Query("SELECT m FROM Metric m WHERE m.hostId = :hostId AND m.metricName = :metricName " +
            "ORDER BY m.time DESC LIMIT 1")
    Metric findLatestMetric(@Param("hostId") Long hostId, @Param("metricName") String metricName);
}
