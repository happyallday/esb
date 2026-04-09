package com.esb.controller;

import com.esb.dto.ProxyRequest;
import com.esb.dto.ProxyResponse;
import com.esb.service.ESBProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/proxy")
public class ESBProxyController {
    
    @Autowired
    private ESBProxyService esbProxyService;

    @PostMapping("/request")
    public ResponseEntity<ProxyResponse> proxyRequest(@RequestBody ProxyRequest request) {
        ProxyResponse response = esbProxyService.proxyRequest(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/request/{service}")
    public ResponseEntity<ProxyResponse> proxyRequestByService(
            @PathVariable String service,
            @RequestParam(defaultValue = "GET") String method,
            @RequestParam(required = false) String path,
            @RequestParam(required = false) String params,
            @RequestBody(required = false) String body) {
        
        ProxyRequest request = new ProxyRequest();
        request.setService(service);
        request.setMethod(method);
        request.setPath(path != null ? path : "");
        request.setParams(params);
        request.setBody(body);
        
        ProxyResponse response = esbProxyService.proxyRequest(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}