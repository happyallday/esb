package com.esb.service;

import com.esb.entity.ServiceConfig;
import com.esb.mapper.ServiceConfigMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ServiceConfigService {
    
    private final ServiceConfigMapper serviceConfigMapper;

    public ServiceConfigService(ServiceConfigMapper serviceConfigMapper) {
        this.serviceConfigMapper = serviceConfigMapper;
    }

    public List<ServiceConfig> getAllActiveServices() {
        return serviceConfigMapper.selectList(null);
    }

    public ServiceConfig getServiceByName(String serviceName) {
        return serviceConfigMapper.selectByServiceName(serviceName);
    }
}