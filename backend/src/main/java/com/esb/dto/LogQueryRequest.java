package com.esb.dto;

import lombok.Data;

@Data
public class LogQueryRequest {
    private String serviceName;
    private String startTime;
    private String endTime;
    private Integer statusCd;
    private String keyword;
    private Integer page = 1;
    private Integer size = 10;
}