package com.alertix.alerts.controller;

import com.alertix.alerts.dto.AlertDTO;
import com.alertix.alerts.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    public ResponseEntity<Page<AlertDTO>> getAllAlerts(Pageable pageable) {
        return ResponseEntity.ok(alertService.getAllAlerts(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertDTO> getAlertById(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.getAlertById(id));
    }

    @PostMapping
    public ResponseEntity<AlertDTO> createAlert(@RequestBody AlertDTO alertDTO) {
        return ResponseEntity.ok(alertService.createAlert(alertDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlertDTO> updateAlertStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(alertService.updateAlertStatus(id, status));
    }

    @GetMapping("/active")
    public ResponseEntity<Page<AlertDTO>> getActiveAlerts(Pageable pageable) {
        return ResponseEntity.ok(alertService.getActiveAlerts(pageable));
    }

    @GetMapping("/host/{hostId}")
    public ResponseEntity<Page<AlertDTO>> getAlertsByHost(
            @PathVariable Long hostId,
            Pageable pageable) {
        return ResponseEntity.ok(alertService.getAlertsByHost(hostId, pageable));
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<Page<AlertDTO>> getAlertsByService(
            @PathVariable Long serviceId,
            Pageable pageable) {
        return ResponseEntity.ok(alertService.getAlertsByService(serviceId, pageable));
    }
}
