package com.esb.controller;

import com.esb.entity.ServiceConfig;
import com.esb.service.ServiceConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/services")
public class ServiceController {
    
    @Autowired
    private ServiceConfigService serviceConfigService;

    @GetMapping("/active")
    public ResponseEntity<List<ServiceConfig>> getActiveServices() {
        List<ServiceConfig> allServices = serviceConfigService.getAllActiveServices();
        List<ServiceConfig> activeServices = allServices.stream()
                .filter(s -> s.getStatus() != null && s.getStatus() == 1)
                .collect(Collectors.toList());
        return ResponseEntity.ok(activeServices);
    }

    @GetMapping("/{serviceName}")
    public ResponseEntity<ServiceConfig> getServiceDetails(@PathVariable String serviceName) {
        ServiceConfig service = serviceConfigService.getServiceByName(serviceName);
        if (service == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(service);
    }
}