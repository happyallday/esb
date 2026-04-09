package com.esb.handler;

import com.alibaba.fastjson.JSON;
import com.esb.dto.ProxyRequest;
import com.esb.dto.ProxyResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SOAPHandler {
    
    private final RestTemplate restTemplate;

    public SOAPHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ProxyResponse handleRequest(ProxyRequest request, String targetUrl) {
        ProxyResponse response = new ProxyResponse();
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_XML);
            headers.add("SOAPAction", request.getHeaders() != null ? 
                    extractSOAPAction(request.getHeaders()) : "");
            
            HttpEntity<String> httpEntity = new HttpEntity<>(request.getBody(), headers);
            
            ResponseEntity<String> exchangeResponse = restTemplate.postForEntity(
                    targetUrl,
                    httpEntity,
                    String.class
            );
            
            response.setStatusCode(exchangeResponse.getStatusCodeValue());
            response.setHeaders(JSON.toJSONString(exchangeResponse.getHeaders().toSingleValueMap()));
            response.setBody(exchangeResponse.getBody());
            
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError("SOAP请求失败: " + e.getMessage());
        }
        
        return response;
    }

    private String extractSOAPAction(String headersJson) {
        try {
            return headersJson.contains("SOAPAction") ? "" : "";
        } catch (Exception e) {
            return "";
        }
    }
}