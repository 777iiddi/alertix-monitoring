package com.alertix.adapters.rest;

import com.alertix.application.service.HostService;
import com.alertix.adapters.rest.dto.HostDto;
import com.alertix.domain.model.Host;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/hosts")
public class HostController {

    private final HostService service;

    public HostController(HostService service) {
        this.service = service;
    }


    @GetMapping
    public ResponseEntity<List<HostDto>> list() {
        List<HostDto> dtos = service.list().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }


    @PostMapping
    public ResponseEntity<HostDto> create(@RequestBody HostDto dto) {
        Host h = toDomain(dto);
        Host created = service.createHost(h);
        HostDto out = toDto(created);
        return ResponseEntity.ok(out);
    }


    private HostDto toDto(Host h) {
        if (h == null) return null;
        return new HostDto(
                h.getId(),
                h.getName(),
                h.getIp(),
                h.getOs(),
                h.getTags()
        );
    }

    private Host toDomain(HostDto dto) {
        if (dto == null) return null;
        Host h = new Host();
        h.setName(dto.getName());
        h.setIp(dto.getIp());
        h.setOs(dto.getOs());
        h.setTags(dto.getTags());
        return h;
    }
}
