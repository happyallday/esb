package com.esb.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class LogListResponse {
    private List<LogData> records;
    private Long total;
    private Integer page;
    private Integer size;

    @Data
    public static class LogData {
        private Long id;
        private String requestId;
        private String serviceName;
        private String methodType;
        private String requestUrl;
        private Integer responseStatus;
        private Long durationMs;
        private LocalDateTime requestTime;
        private LocalDateTime responseTime;
        private String errorMessage;
    }
}