package com.alertix.adapters.rest.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter

public class HostDto {

    private UUID id;
    private String name;
    private String ip;
    private String os;
    private List<String> tags;

    public HostDto() {
    }

    public HostDto(UUID id, String name, String ip, String os, List<String> tags) {
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.os = os;
        this.tags = tags;
    }


}
