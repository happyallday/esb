package com.esb.controller;

import com.esb.dto.MetricsResponse;
import com.esb.service.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metrics")
public class MetricsController {
    
    @Autowired
    private MetricsService metricsService;

    @GetMapping
    public ResponseEntity<MetricsResponse> getMetrics() {
        MetricsResponse response = metricsService.getMetrics();
        return ResponseEntity.ok(response);
    }
}