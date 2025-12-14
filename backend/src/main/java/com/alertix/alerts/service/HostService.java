package com.alertix.alerts.service;

import com.alertix.alerts.entity.Host;
import com.alertix.alerts.exception.ResourceNotFoundException;
import com.alertix.alerts.repository.HostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HostService {
    
    private final HostRepository hostRepository;
    
    public Host getHostById(Long id) {
        return hostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Host", "id", id));
    }
}
