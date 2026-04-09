package com.esb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("esb_service_config")
public class ServiceConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String serviceName;
    private String serviceType;
    private String targetUrl;
    private Integer timeout;
    private Integer rateLimit;
    private Integer circuitOpenThreshold;
    private Integer status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}