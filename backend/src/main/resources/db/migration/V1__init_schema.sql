-- 接口请求日志表
CREATE TABLE IF NOT EXISTS esb_request_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_id VARCHAR(64) NOT NULL COMMENT '请求ID',
    service_name VARCHAR(128) NOT NULL COMMENT '服务名称',
    method_type VARCHAR(8) NOT NULL COMMENT '请求方法',
    request_url VARCHAR(1024) NOT NULL COMMENT '请求URL',
    request_headers TEXT COMMENT '请求头JSON',
    request_params TEXT COMMENT '请求参数JSON',
    request_body LONGTEXT COMMENT '请求体JSON',
    response_status INT COMMENT '响应状态码',
    response_headers TEXT COMMENT '响应头JSON',
    response_body LONGTEXT COMMENT '响应体JSON',
    request_time DATETIME(3) NOT NULL COMMENT '请求时间',
    response_time DATETIME(3) COMMENT '响应时间',
    duration_ms BIGINT COMMENT '耗时毫秒',
    error_message TEXT COMMENT '错误信息',
    created_time DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3),
    INDEX idx_service_name (service_name),
    INDEX idx_request_time (request_time),
    INDEX idx_response_status (response_status),
    INDEX idx_request_id (request_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ESB接口调用日志表';

-- 服务配置表
CREATE TABLE IF NOT EXISTS esb_service_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    service_name VARCHAR(128) NOT NULL UNIQUE COMMENT '服务名称',
    service_type VARCHAR(16) NOT NULL COMMENT '服务类型:REST/SOAP',
    target_url VARCHAR(512) NOT NULL COMMENT '目标地址',
    timeout INT DEFAULT 30000 COMMENT '超时时间ms',
    rate_limit INT DEFAULT 100 COMMENT '限流阈值',
    circuit_open_threshold INT DEFAULT 50 COMMENT '熔断阈值百分比',
    status TINYINT DEFAULT 1 COMMENT '状态:1启用,0禁用',
    created_time DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3),
    updated_time DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    INDEX idx_service_name (service_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ESB服务配置表';

-- 插入测试数据
INSERT INTO esb_service_config (service_name, service_type, target_url, timeout, rate_limit, circuit_open_threshold, status) VALUES
('user-service', 'REST', 'http://localhost:8001', 5000, 100, 50, 1),
('order-service', 'REST', 'http://localhost:8002', 8000, 150, 50, 1),
('payment-service', 'SOAP', 'http://localhost:8003/soap', 10000, 50, 40, 1);