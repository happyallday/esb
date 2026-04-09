package com.esb.service;

import com.esb.entity.RequestLog;
import com.esb.mapper.RequestLogMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class RequestLogService {
    
    private final RequestLogMapper requestLogMapper;

    public RequestLogService(RequestLogMapper requestLogMapper) {
        this.requestLogMapper = requestLogMapper;
    }

    @Async
    public void saveLog(RequestLog log) {
        requestLogMapper.insert(log);
    }

    public RequestLog getLogById(Long id) {
        return requestLogMapper.selectById(id);
    }
}