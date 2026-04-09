package com.esb.service;

import com.esb.dto.MetricsResponse;
import com.esb.entity.RequestLog;
import com.esb.mapper.RequestLogMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MetricsService {
    
    private final RequestLogMapper requestLogMapper;

    public MetricsService(RequestLogMapper requestLogMapper) {
        this.requestLogMapper = requestLogMapper;
    }

    public MetricsResponse getMetrics() {
        MetricsResponse response = new MetricsResponse();
        
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().plusDays(1).atStartOfDay();
        
        List<RequestLog> todayLogs = requestLogMapper.selectList(
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper.<RequestLog>lambdaQuery()
                .ge(RequestLog::getRequestTime, todayStart)
                .lt(RequestLog::getRequestTime, todayEnd)
        );
        
        long totalCalls = todayLogs.size();
        long successCalls = todayLogs.stream().filter(log -> log.getResponseStatus() != null && 
            log.getResponseStatus() >= 200 && log.getResponseStatus() < 300).count();
        long failedCalls = totalCalls - successCalls;
        
        response.setTodayTotalCalls(totalCalls);
        response.setTodaySuccessCalls(successCalls);
        response.setTodayFailedCalls(failedCalls);
        response.setSuccessRate(totalCalls > 0 ? (successCalls * 100.0 / totalCalls) : 0.0);
        
        long totalResponseTime = todayLogs.stream()
            .filter(log -> log.getDurationMs() != null)
            .mapToLong(RequestLog::getDurationMs)
            .sum();
        long validLogs = todayLogs.stream().filter(log -> log.getDurationMs() != null).count();
        response.setAvgResponseTime(validLogs > 0 ? totalResponseTime / validLogs : 0);
        
        response.setCallTrend(getCallTrend());
        response.setServiceStats(getServiceStats());
        
        return response;
    }

    private List<MetricsResponse.CallTrendData> getCallTrend() {
        List<MetricsResponse.CallTrendData> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
            
            long count = requestLogMapper.selectCount(
                com.baomidou.mybatisplus.core.conditions.query.QueryWrapper.<RequestLog>lambdaQuery()
                    .ge(RequestLog::getRequestTime, dayStart)
                    .lt(RequestLog::getRequestTime, dayEnd)
            );
            
            MetricsResponse.CallTrendData data = new MetricsResponse.CallTrendData();
            data.setDate(date.format(formatter));
            data.setCount(count);
            trend.add(data);
        }
        
        return trend;
    }

    private List<MetricsResponse.ServiceStatsData> getServiceStats() {
        List<RequestLog> allLogs = requestLogMapper.selectList(null);
        
        Map<String, Long> serviceCountMap = allLogs.stream()
            .collect(Collectors.groupingBy(RequestLog::getServiceName, Collectors.counting()));
        
        List<MetricsResponse.ServiceStatsData> stats = new ArrayList<>();
        serviceCountMap.forEach((service, count) -> {
            MetricsResponse.ServiceStatsData data = new MetricsResponse.ServiceStatsData();
            data.setService(service);
            data.setCount(count);
            stats.add(data);
        });
        
        stats.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));
        return stats;
    }
}