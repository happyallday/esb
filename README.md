# ESB Gateway - 接口中转服务管理系统

一个功能完善的ESB接口中转服务，支持REST和SOAP协议转发，提供完整的调用日志记录和可视化管理界面。

## 项目概述

ESB Gateway是一个企业级接口代理服务，具有以下核心功能：

- 🔄 **接口转发**：支持REST和SOAP协议的接口中转
- 📊 **日志记录**：完整记录所有接口调用的请求和响应信息
- 🛡️ **限流熔断**：集成Resilience4j实现限流和熔断保护
- 📈 **数据统计**：提供调用趋势、成功率、平均响应时间等统计
- 🔍 **日志查询**：支持多条件查询和详细日志查看
- 🎨 **管理界面**：基于Vue3 + Element Plus的现代化管理界面

## 技术栈

### 后端
- **Java 11** - 编程语言
- **Spring Boot 2.7.18** - 应用框架
- **MyBatis Plus 3.5.5** - 数据持久层
- **MySQL 5.7** - 数据库
- **Resilience4j 2.1.0** - 限流熔断组件
- **Fastjson 2.0.43** - JSON处理

### 前端
- **Vue 3** - 前端框架
- **TypeScript** - 类型支持
- **Element Plus** - UI组件库
- **ECharts 5.4.3** - 图表库
- **Axios** - HTTP客户端
- **Vite** - 构建工具

## 项目结构

```
esb/
├── backend/                      # 后端项目
│   ├── src/main/java/com/esb/
│   │   ├── ESBApplication.java  # 主应用类
│   │   ├── config/              # 配置类
│   │   ├── controller/          # 控制器
│   │   ├── service/             # 服务层
│   │   ├── handler/             # 协议处理器
│   │   ├── entity/              # 实体类
│   │   ├── mapper/              # 数据访问层
│   │   └── dto/                 # 数据传输对象
│   └── src/main/resources/
│       ├── application.yml      # 应用配置
│       └── mapper/              # MyBatis映射文件
├── frontend/                    # 前端项目
│   ├── src/
│   │   ├── views/               # 页面组件
│   │   ├── api/                 # API接口
│   │   ├── utils/               # 工具类
│   │   └── router/              # 路由配置
│   ├── package.json
│   └── vite.config.ts
└── README.md                    # 本文件
```

## 环境要求

### 后端环境
- JDK 11 或更高版本
- Maven 3.6 或更高版本
- MySQL 5.7

### 前端环境
- Node.js 16 或更高版本
- npm 或 yarn

## 数据库配置

### 1. 创建数据库
```sql
CREATE DATABASE esb_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

### 2. 配置数据库连接

修改 `backend/src/main/resources/application.yml` 文件：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/esb_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

首次启动时，系统会自动创建表结构和初始数据。

## 启动方法

### 启动后端服务

#### 方法一：使用Maven启动
```bash
cd backend
mvn spring-boot:run
```

#### 方法二：打包后启动
```bash
cd backend
mvn clean package
java -jar target/esb-gateway-1.0.0.jar
```

后端服务将在 `http://localhost:8080` 启动，API基础路径为 `/api`。

### 启动前端服务

#### 安装依赖
```bash
cd frontend
npm install
```

#### 启动开发服务器
```bash
npm run dev
```

前端服务将在 `http://localhost:3000` 启动。

## 访问管理界面

启动前后端服务后，在浏览器中访问：http://localhost:3000

管理界面包含以下功能模块：

### 1. 仪表盘
- 今日调用统计（总数、成功数、失败数、成功率）
- 调用趋势图表（最近7天）
- 服务调用分布图

### 2. 调用日志
- 日志列表查询（支持服务名、状态码、时间范围、关键词过滤）
- 分页查看
- 点击查看详细日志

### 3. 日志详情
- 基本信息：请求ID、服务名、方法类型、状态码、耗时
- 完整的请求和响应数据
- JSON格式化的查看器
- 错误信息展示

## API接口说明

### 1. 代理接口

**请求示例：**
```bash
curl -X POST http://localhost:8080/api/proxy/request \
-H "Content-Type: application/json" \
-d '{
  "service": "user-service",
  "method": "GET",
  "path": "/api/users",
  "params": "{\"page\":\"1\",\"size\":\"10\"}",
  "headers": "{\"Authorization\":\"Bearer token\"}"
}'
```

### 2. 统计接口

**获取统计数据：**
```bash
curl http://localhost:8080/api/metrics
```

### 3. 日志查询接口

**查询日志列表：**
```bash
curl "http://localhost:8080/api/logs?page=1&size=10&serviceName=user-service"
```

**获取日志详情：**
```bash
curl http://localhost:8080/api/logs/1
```

## 限流熔断配置

系统默认配置了以下限流和熔断策略：

- **限流**：每秒100次请求
- **熔断**：失败率超过50%触发熔断
- **重试**：支持自动重试（最多3次）

可在 `application.yml` 中调整配置：

```yaml
resilience4j:
  ratelimiter:
    instances:
      esbRateLimiter:
        limit-for-period: 100        # 限流阈值
        limit-refresh-period: 1s     # 刷新周期
  circuitbreaker:
    instances:
      esbCircuitBreaker:
        failure-rate-threshold: 50   # 熔断阈值
        wait-duration-in-open-state: 10s  # 熔断等待时间
```

## 开发说明

### 后端开发
- 遵循Spring Boot最佳实践
- 使用MyBatis Plus简化数据库操作
- 异步记录日志提高性能

### 前端开发
- 基于Composition API和TypeScript
- 响应式设计，支持不同屏幕尺寸
- 使用ECharts实现数据可视化

## 故障排除

### 数据库连接失败
- 检查MySQL服务是否启动
- 验证数据库连接配置是否正确
- 确保数据库用户有相应权限

### 前端无法连接后端
- 确认后端服务已启动
- 检查防火墙设置
- 查看浏览器控制台错误信息

### 日志写入失败
- 检查数据库连接
- 查看后端日志错误信息
- 验证表结构是否正确创建

## 分支策略

项目使用Git进行版本控制，主要的里程碑包括：

- **Milestone 1**: 项目初始化 - 基础项目框架搭建
- **Milestone 2**: 数据库层完成 - 数据库表结构和数据访问层
- **Milestone 3**: 后端核心接口完成 - 接口转发核心功能
- **Milestone 4**: 后端查询接口完成 - 统计和日志查询API
- **Milestone 5**: 前端基础架构完成 - Vue3项目基础框架
- **Milestone 7**: 集成测试完成 - 前后端联调和文档完善

## 许可证

本项目采用MIT许可证。

## 联系方式

如有问题或建议，请提交Issue或Pull Request。
