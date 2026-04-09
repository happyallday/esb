package com.esb.controller;

import com.esb.dto.LogListResponse;
import com.esb.dto.LogQueryRequest;
import com.esb.entity.RequestLog;
import com.esb.service.LogQueryService;
import com.esb.service.RequestLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/logs")
public class LogController {
    
    @Autowired
    private LogQueryService logQueryService;
    
    @Autowired
    private RequestLogService requestLogService;

    @GetMapping
    public ResponseEntity<LogListResponse> getLogs(LogQueryRequest queryRequest) {
        LogListResponse response = logQueryService.queryLogs(queryRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequestLog> getLogDetail(@PathVariable Long id) {
        RequestLog log = requestLogService.getLogById(id);
        if (log == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(log);
    }
}