package com.alertix.alerts.service;

import com.alertix.alerts.dto.AlertDTO;
import com.alertix.alerts.entity.Alert;
import com.alertix.alerts.entity.AlertStatus;
import com.alertix.alerts.exception.ResourceNotFoundException;
import com.alertix.alerts.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final AlertRuleService alertRuleService;
    private final HostService hostService;
    private final MonitoredService monitoredService;

    @Transactional(readOnly = true)
    public Page<AlertDTO> getAllAlerts(Pageable pageable) {
        return alertRepository.findAll(pageable).map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public AlertDTO getAlertById(Long id) {
        return alertRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with id: " + id));
    }

    @Transactional
    public AlertDTO createAlert(AlertDTO alertDTO) {
        Alert alert = new Alert();
        updateAlertFromDTO(alert, alertDTO);
        alert.setStatus(AlertStatus.FIRING);
        alert.setTriggeredAt(java.time.LocalDateTime.now());
        return convertToDTO(alertRepository.save(alert));
    }

    @Transactional
    public AlertDTO updateAlertStatus(Long id, String status) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with id: " + id));
        
        AlertStatus newStatus = AlertStatus.valueOf(status.toUpperCase());
        alert.setStatus(newStatus);
        
        if (newStatus == AlertStatus.RESOLVED) {
            alert.setResolvedAt(java.time.LocalDateTime.now());
        }
        
        return convertToDTO(alertRepository.save(alert));
    }

    @Transactional(readOnly = true)
    public Page<AlertDTO> getActiveAlerts(Pageable pageable) {
        return alertRepository.findByStatus(AlertStatus.FIRING, pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<AlertDTO> getAlertsByHost(Long hostId, Pageable pageable) {
        return alertRepository.findByHostId(hostId, pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<AlertDTO> getAlertsByService(Long serviceId, Pageable pageable) {
        return alertRepository.findByServiceId(serviceId, pageable)
                .map(this::convertToDTO);
    }

    private AlertDTO convertToDTO(Alert alert) {
        return AlertDTO.builder()
                .id(alert.getId())
                .ruleId(alert.getRule().getId())
                .hostId(alert.getHost() != null ? alert.getHost().getId() : null)
                .serviceId(alert.getService() != null ? alert.getService().getId() : null)
                .message(alert.getMessage())
                .value(alert.getValue())
                .status(alert.getStatus())
                .severity(alert.getSeverity())
                .triggeredAt(alert.getTriggeredAt())
                .resolvedAt(alert.getResolvedAt())
                .build();
    }

    private void updateAlertFromDTO(Alert alert, AlertDTO alertDTO) {
        alert.setRule(alertRuleService.getAlertRuleById(alertDTO.getRuleId()));
        
        if (alertDTO.getHostId() != null) {
            alert.setHost(hostService.getHostById(alertDTO.getHostId()));
        }
        
        if (alertDTO.getServiceId() != null) {
            alert.setService(monitoredService.getServiceById(alertDTO.getServiceId()));
        }
        
        alert.setMessage(alertDTO.getMessage());
        alert.setValue(alertDTO.getValue());
        alert.setSeverity(alertDTO.getSeverity());
    }
}
