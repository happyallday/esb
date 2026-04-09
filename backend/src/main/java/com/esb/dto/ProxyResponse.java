package com.esb.dto;

import lombok.Data;

@Data
public class ProxyResponse {
    private int statusCode;
    private String headers;
    private String body;
    private String error;
}