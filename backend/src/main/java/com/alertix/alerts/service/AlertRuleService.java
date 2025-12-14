package com.alertix.alerts.service;

import com.alertix.alerts.dto.AlertRuleDTO;
import com.alertix.alerts.entity.AlertRule;
import com.alertix.alerts.entity.Host;
import com.alertix.alerts.entity.MonitoredService;
import com.alertix.alerts.exception.ResourceNotFoundException;
import com.alertix.alerts.repository.AlertRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlertRuleService {

    private final AlertRuleRepository alertRuleRepository;
    private final HostService hostService;
    private final MonitoredService monitoredService;

    @Transactional(readOnly = true)
    public Page<AlertRuleDTO> getAllAlertRules(Pageable pageable) {
        return alertRuleRepository.findAll(pageable).map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public AlertRule getAlertRuleById(Long id) {
        return alertRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AlertRule", "id", id));
    }

    @Transactional
    public AlertRuleDTO createAlertRule(AlertRuleDTO alertRuleDTO) {
        AlertRule alertRule = new AlertRule();
        updateAlertRuleFromDTO(alertRule, alertRuleDTO);
        return convertToDTO(alertRuleRepository.save(alertRule));
    }

    @Transactional
    public AlertRuleDTO updateAlertRule(Long id, AlertRuleDTO alertRuleDTO) {
        AlertRule alertRule = getAlertRuleById(id);
        updateAlertRuleFromDTO(alertRule, alertRuleDTO);
        return convertToDTO(alertRuleRepository.save(alertRule));
    }

    @Transactional
    public void deleteAlertRule(Long id) {
        AlertRule alertRule = getAlertRuleById(id);
        alertRuleRepository.delete(alertRule);
    }

    @Transactional
    public AlertRuleDTO setAlertRuleStatus(Long id, boolean enabled) {
        AlertRule alertRule = getAlertRuleById(id);
        alertRule.setEnabled(enabled);
        return convertToDTO(alertRuleRepository.save(alertRule));
    }

    private AlertRuleDTO convertToDTO(AlertRule alertRule) {
        return AlertRuleDTO.builder()
                .id(alertRule.getId())
                .name(alertRule.getName())
                .hostId(alertRule.getHost() != null ? alertRule.getHost().getId() : null)
                .serviceId(alertRule.getService() != null ? alertRule.getService().getId() : null)
                .condition(alertRule.getCondition())
                .threshold(alertRule.getThreshold())
                .duration(alertRule.getDuration())
                .severity(alertRule.getSeverity())
                .enabled(alertRule.getEnabled())
                .description(alertRule.getDescription())
                .build();
    }

    private void updateAlertRuleFromDTO(AlertRule alertRule, AlertRuleDTO dto) {
        if (dto.getName() != null) {
            alertRule.setName(dto.getName());
        }
        
        if (dto.getHostId() != null) {
            Host host = hostService.getHostById(dto.getHostId());
            alertRule.setHost(host);
        } else {
            alertRule.setHost(null);
        }
        
        if (dto.getServiceId() != null) {
            MonitoredService service = monitoredService.getServiceById(dto.getServiceId());
            alertRule.setService(service);
        } else {
            alertRule.setService(null);
        }
        
        if (dto.getCondition() != null) {
            alertRule.setCondition(dto.getCondition());
        }
        
        if (dto.getThreshold() != null) {
            alertRule.setThreshold(dto.getThreshold());
        }
        
        if (dto.getDuration() != null) {
            alertRule.setDuration(dto.getDuration());
        }
        
        if (dto.getSeverity() != null) {
            alertRule.setSeverity(dto.getSeverity());
        }
        
        if (dto.getEnabled() != null) {
            alertRule.setEnabled(dto.getEnabled());
        }
        
        if (dto.getDescription() != null) {
            alertRule.setDescription(dto.getDescription());
        }
    }
}
