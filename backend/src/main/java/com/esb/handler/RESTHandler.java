package com.esb.handler;

import com.alibaba.fastjson.JSON;
import com.esb.dto.ProxyRequest;
import com.esb.dto.ProxyResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

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
            
            if (request.getHeaders() != null && !request.getHeaders().trim().isEmpty()) {
                try {
                    Map<String, String> headerMap = JSON.parseObject(request.getHeaders(), Map.class);
                    if (headerMap != null) {
                        headerMap.forEach((key, value) -> {
                            if (key != null && value != null) {
                                headers.set(key, value);
                            }
                        });
                    }
                } catch (Exception e) {
                    return createErrorResponse(400, JSON.toJSONString(Map.of(
                        "error", "请求头格式错误",
                        "message", e.getMessage(),
                        "details", "请检查请求头是否为有效的JSON格式"
                    )));
                }
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
            
        } catch (HttpClientErrorException e) {
            response.setStatusCode(e.getStatusCode().value());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "客户端请求错误");
            errorResponse.put("statusCode", e.getStatusCode().value());
            errorResponse.put("message", e.getMessage());
            errorResponse.put("responseBody", e.getResponseBodyAsString());
            errorResponse.put("headers", getErrorHeaders(e.getResponseHeaders()));
            response.setBody(JSON.toJSONString(errorResponse, true));
            
        } catch (HttpServerErrorException e) {
            response.setStatusCode(e.getStatusCode().value());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "服务器端错误");
            errorResponse.put("statusCode", e.getStatusCode().value());
            errorResponse.put("message", e.getMessage());
            errorResponse.put("responseBody", e.getResponseBodyAsString());
            errorResponse.put("headers", getErrorHeaders(e.getResponseHeaders()));
            response.setBody(JSON.toJSONString(errorResponse, true));
            
        } catch (ResourceAccessException e) {
            response.setStatusCode(503);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "网络连接失败");
            errorResponse.put("statusCode", 503);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("details", "无法连接到目标服务器，请检查网络连接或目标URL是否正确");
            if (e.getCause() != null) {
                errorResponse.put("cause", e.getCause().getMessage());
            }
            response.setBody(JSON.toJSONString(errorResponse, true));
            
        } catch (RestClientException e) {
            response.setStatusCode(502);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "REST客户端错误");
            errorResponse.put("statusCode", 502);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("details", "请求处理过程中发生错误");
            response.setBody(JSON.toJSONString(errorResponse, true));
            
        } catch (Exception e) {
            response.setStatusCode(500);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "代理请求失败");
            errorResponse.put("statusCode", 500);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("exceptionType", e.getClass().getSimpleName());
            errorResponse.put("stackTrace", getStackTrace(e));
            response.setBody(JSON.toJSONString(errorResponse, true));
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
        String requestBody = request.getBody();
        if (requestBody != null && !requestBody.trim().isEmpty() && !"{}".equals(requestBody.trim()) &&
                !HttpMethod.GET.name().equals(request.getMethod())) {
            return new HttpEntity<>(requestBody, headers);
        }
        return new HttpEntity<>(headers);
    }
    
    private ProxyResponse createErrorResponse(int statusCode, String errorBody) {
        ProxyResponse response = new ProxyResponse();
        response.setStatusCode(statusCode);
        response.setBody(errorBody);
        return response;
    }
    
    private Map<String, String> getErrorHeaders(HttpHeaders headers) {
        Map<String, String> result = new HashMap<>();
        if (headers != null) {
            headers.forEach((key, value) -> {
                if (value != null && !value.isEmpty()) {
                    result.put(key, value.get(0));
                }
            });
        }
        return result;
    }
    
    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}