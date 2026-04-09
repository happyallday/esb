package com.esb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.esb.entity.RequestLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface RequestLogMapper extends BaseMapper<RequestLog> {
    
    IPage<RequestLog> getLogPage(
        Page<RequestLog> page,
        @Param("serviceName") String serviceName,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("statusCd") Integer statusCd,
        @Param("keyword") String keyword
    );
}