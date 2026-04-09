package com.esb.service;

import com.alibaba.fastjson.JSON;
import com.esb.dto.ProxyRequest;
import com.esb.dto.ProxyResponse;
import com.esb.entity.RequestLog;
import com.esb.entity.ServiceConfig;
import com.esb.handler.RESTHandler;
import com.esb.handler.SOAPHandler;
import com.esb.mapper.ServiceConfigMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ESBProxyService {
    
    private final RESTHandler restHandler;
    private final SOAPHandler soapHandler;
    private final ServiceConfigMapper serviceConfigMapper;
    private final RequestLogService requestLogService;
    private final RateLimiterRegistry rateLimiterRegistry;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public ESBProxyService(RESTHandler restHandler, SOAPHandler soapHandler,
                          ServiceConfigMapper serviceConfigMapper,
                          RequestLogService requestLogService,
                          RateLimiterRegistry rateLimiterRegistry,
                          CircuitBreakerRegistry circuitBreakerRegistry) {
        this.restHandler = restHandler;
        this.soapHandler = soapHandler;
        this.serviceConfigMapper = serviceConfigMapper;
        this.requestLogService = requestLogService;
        this.rateLimiterRegistry = rateLimiterRegistry;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    public ProxyResponse proxyRequest(ProxyRequest request) {
        ProxyResponse response = new ProxyResponse();
        String requestId = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime requestTime = LocalDateTime.now();

        try {
            ServiceConfig serviceConfig = serviceConfigMapper.selectByServiceName(request.getService());
            if (serviceConfig == null) {
                response.setStatusCode(404);
                response.setError("服务不存在: " + request.getService());
                saveLog(requestId, request, response, requestTime, null, null);
                return response;
            }

            RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("esbRateLimiter");
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("esbCircuitBreaker");

            response = CircuitBreaker.decorateSupplier(circuitBreaker, () ->
                RateLimiter.decorateSupplier(rateLimiter, () -> {
                    if ("REST".equals(serviceConfig.getServiceType())) {
                        return restHandler.handleRequest(request, serviceConfig.getTargetUrl());
                    } else if ("SOAP".equals(serviceConfig.getServiceType())) {
                        return soapHandler.handleRequest(request, serviceConfig.getTargetUrl());
                    }
                    return new ProxyResponse();
                }).get()
            ).get();

            LocalDateTime responseTime = LocalDateTime.now();
            saveLog(requestId, request, response, requestTime, responseTime, null);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError("代理异常: " + e.getMessage());
            saveLog(requestId, request, response, requestTime, null, e.getMessage());
        }

        return response;
    }

    @Async
    public void saveLog(String requestId, ProxyRequest request, ProxyResponse response,
                       LocalDateTime requestTime, LocalDateTime responseTime, String errorMessage) {
        RequestLog log = new RequestLog();
        log.setRequestId(requestId);
        log.setServiceName(request.getService());
        log.setMethodType(request.getMethod());
        log.setRequestUrl(request.getPath());
        log.setRequestHeaders(request.getHeaders());
        log.setRequestParams(request.getParams());
        log.setRequestBody(request.getBody());
        log.setResponseStatus(response.getStatusCode());
        log.setResponseHeaders(response.getHeaders());
        log.setResponseBody(response.getBody());
        log.setRequestTime(requestTime);
        log.setResponseTime(responseTime);
        log.setDurationMs(responseTime != null ? 
            java.time.Duration.between(requestTime, responseTime).toMillis() : null);
        log.setErrorMessage(errorMessage);

        requestLogService.saveLog(log);
    }
}