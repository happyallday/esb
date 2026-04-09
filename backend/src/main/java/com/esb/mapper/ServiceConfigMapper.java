package com.esb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.esb.entity.ServiceConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ServiceConfigMapper extends BaseMapper<ServiceConfig> {
    
    ServiceConfig selectByServiceName(@Param("serviceName") String serviceName);
}