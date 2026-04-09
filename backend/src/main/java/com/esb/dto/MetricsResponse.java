package com.esb.dto;

import lombok.Data;
import java.util.List;

@Data
public class MetricsResponse {
    private Long todayTotalCalls;
    private Long todaySuccessCalls;
    private Long todayFailedCalls;
    private Double successRate;
    private Long avgResponseTime;
    private List<CallTrendData> callTrend;
    private List<ServiceStatsData> serviceStats;

    @Data
    public static class CallTrendData {
        private String date;
        private Long count;
    }

    @Data
    public static class ServiceStatsData {
        private String service;
        private Long count;
    }
}