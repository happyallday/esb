package com.esb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("esb_request_log")
public class RequestLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String requestId;
    private String serviceName;
    private String methodType;
    private String requestUrl;
    private String requestHeaders;
    private String requestParams;
    private String requestBody;
    private Integer responseStatus;
    private String responseHeaders;
    private String responseBody;
    private LocalDateTime requestTime;
    private LocalDateTime responseTime;
    private Long durationMs;
    private String errorMessage;
    private LocalDateTime createdTime;
}