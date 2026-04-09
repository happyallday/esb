package com.esb.handler;

import com.alibaba.fastjson.JSON;
import com.esb.dto.ProxyRequest;
import com.esb.dto.ProxyResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Component
public class RESTHandler {
    
    private final RestTemplate restTemplate;

    public RESTHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ProxyResponse handleRequest(ProxyRequest request, String targetUrl) {
        ProxyResponse response = new ProxyResponse();
        
        try {
            HttpHeaders headers = new HttpHeaders();
            if (request.getHeaders() != null) {
                Map<String, String> headerMap = JSON.parseObject(request.getHeaders(), Map.class);
                headerMap.forEach(headers::add);
            }
            
            String fullUrl = buildFullUrl(targetUrl, request.getPath(), request.getParams());
            
            HttpEntity<?> httpEntity = buildHttpEntity(request, headers);
            HttpMethod method = HttpMethod.resolve(request.getMethod());
            
            ResponseEntity<String> exchangeResponse = restTemplate.exchange(
                    fullUrl,
                    method,
                    httpEntity,
                    String.class
            );
            
            response.setStatusCode(exchangeResponse.getStatusCodeValue());
            response.setHeaders(JSON.toJSONString(exchangeResponse.getHeaders().toSingleValueMap()));
            response.setBody(exchangeResponse.getBody());
            
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError("代理请求失败: " + e.getMessage());
        }
        
        return response;
    }

    private String buildFullUrl(String targetUrl, String path, String params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(targetUrl + path);
        
        if (params != null) {
            Map<String, String> paramMap = JSON.parseObject(params, Map.class);
            paramMap.forEach(builder::queryParam);
        }
        
        return builder.build().toUriString();
    }

    private HttpEntity<?> buildHttpEntity(ProxyRequest request, HttpHeaders headers) {
        if (request.getBody() != null && !request.getBody().isEmpty() && 
                !HttpMethod.GET.name().equals(request.getMethod())) {
            return new HttpEntity<>(request.getBody(), headers);
        }
        return new HttpEntity<>(headers);
    }
}