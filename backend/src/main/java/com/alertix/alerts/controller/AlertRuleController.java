package com.alertix.alerts.controller;

import com.alertix.alerts.dto.AlertRuleDTO;
import com.alertix.alerts.service.AlertRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alert-rules")
@RequiredArgsConstructor
public class AlertRuleController {

    private final AlertRuleService alertRuleService;

    @GetMapping
    public ResponseEntity<Page<AlertRuleDTO>> getAllAlertRules(Pageable pageable) {
        return ResponseEntity.ok(alertRuleService.getAllAlertRules(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertRuleDTO> getAlertRuleById(@PathVariable Long id) {
        return ResponseEntity.ok(alertRuleService.getAlertRuleById(id));
    }

    @PostMapping
    public ResponseEntity<AlertRuleDTO> createAlertRule(@RequestBody AlertRuleDTO alertRuleDTO) {
        return ResponseEntity.ok(alertRuleService.createAlertRule(alertRuleDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlertRuleDTO> updateAlertRule(
            @PathVariable Long id,
            @RequestBody AlertRuleDTO alertRuleDTO) {
        return ResponseEntity.ok(alertRuleService.updateAlertRule(id, alertRuleDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlertRule(@PathVariable Long id) {
        alertRuleService.deleteAlertRule(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/enable")
    public ResponseEntity<AlertRuleDTO> enableAlertRule(@PathVariable Long id) {
        return ResponseEntity.ok(alertRuleService.setAlertRuleStatus(id, true));
    }

    @PutMapping("/{id}/disable")
    public ResponseEntity<AlertRuleDTO> disableAlertRule(@PathVariable Long id) {
        return ResponseEntity.ok(alertRuleService.setAlertRuleStatus(id, false));
    }
}
