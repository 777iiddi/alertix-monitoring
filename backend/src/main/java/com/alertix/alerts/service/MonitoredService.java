package com.alertix.alerts.service;

import com.alertix.alerts.entity.MonitoredService;
import com.alertix.alerts.exception.ResourceNotFoundException;
import com.alertix.alerts.repository.MonitoredServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MonitoredService {
    
    private final MonitoredServiceRepository monitoredServiceRepository;
    
    public MonitoredService getServiceById(Long id) {
        return monitoredServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MonitoredService", "id", id));
    }
}
