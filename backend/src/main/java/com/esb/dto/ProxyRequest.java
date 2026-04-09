package com.esb.dto;

import lombok.Data;

@Data
public class ProxyRequest {
    private String service;
    private String method;
    private String path;
    private String headers;
    private String params;
    private String body;
}