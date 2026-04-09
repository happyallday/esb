package com.esb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esb.dto.LogListResponse;
import com.esb.dto.LogQueryRequest;
import com.esb.entity.RequestLog;
import com.esb.mapper.RequestLogMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class LogQueryService {
    
    private final RequestLogMapper requestLogMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LogQueryService(RequestLogMapper requestLogMapper) {
        this.requestLogMapper = requestLogMapper;
    }

    public LogListResponse queryLogs(LogQueryRequest queryRequest) {
        Page<RequestLog> page = new Page<>(queryRequest.getPage(), queryRequest.getSize());
        
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        
        if (queryRequest.getStartTime() != null && !queryRequest.getStartTime().isEmpty()) {
            startTime = LocalDateTime.parse(queryRequest.getStartTime(), formatter);
        }
        if (queryRequest.getEndTime() != null && !queryRequest.getEndTime().isEmpty()) {
            endTime = LocalDateTime.parse(queryRequest.getEndTime(), formatter);
        }
        
        IPage<RequestLog> result = requestLogMapper.getLogPage(
            page,
            queryRequest.getServiceName(),
            startTime,
            endTime,
            queryRequest.getStatusCd(),
            queryRequest.getKeyword()
        );
        
        LogListResponse response = new LogListResponse();
        response.setRecords(convertToLogData(result.getRecords()));
        response.setTotal(result.getTotal());
        response.setPage((int) result.getCurrent());
        response.setSize((int) result.getSize());
        
        return response;
    }

    private List<LogListResponse.LogData> convertToLogData(List<RequestLog> logs) {
        List<LogListResponse.LogData> result = new ArrayList<>();
        for (RequestLog log : logs) {
            LogListResponse.LogData data = new LogListResponse.LogData();
            data.setId(log.getId());
            data.setRequestId(log.getRequestId());
            data.setServiceName(log.getServiceName());
            data.setMethodType(log.getMethodType());
            data.setRequestUrl(log.getRequestUrl());
            data.setResponseStatus(log.getResponseStatus());
            data.setDurationMs(log.getDurationMs());
            data.setRequestTime(log.getRequestTime());
            data.setResponseTime(log.getResponseTime());
            data.setErrorMessage(log.getErrorMessage());
            result.add(data);
        }
        return result;
    }
}