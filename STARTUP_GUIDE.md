# ESB Gateway 启动指南

## 环境要求

### 后端环境必需组件
- **Java 11 或更高版本**
- **Maven 3.6 或更高版本**
- **MySQL 5.7 数据库**

### 前端环境必需组件
- **Node.js 16 或更高版本**
- **npm 或 yarn**

## 快速启动

### 1. 数据库配置

#### 创建数据库
```sql
CREATE DATABASE esb_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

#### 更新数据库连接配置
编辑 `backend/src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/esb_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_mysql_username
    password: your_mysql_password
```

### 2. 启动后端服务

#### 方式一：使用Maven启动（推荐开发环境）
```bash
cd backend
mvn spring-boot:run
```

#### 方式二：打包后启动
```bash
cd backend
mvn clean package
java -jar target/esb-gateway-1.0.0.jar
```

后端服务将在 `http://localhost:8080` 启动，API基础路径为 `/api`

### 3. 启动前端服务

#### 安装依赖
```bash
cd frontend
npm install
```

#### 启动开发服务器
```bash
npm run dev
```

前端服务将在 `http://localhost:3000` 启动

### 4. 访问应用

打开浏览器访问：http://localhost:3000

## 功能使用

### 接口测试页面
访问：http://localhost:3000/test

1. 选择GLM服务进行测试
2. 点击"加载示例"按钮自动填充配置
3. 修改请求内容后点击"发送请求"
4. 查看响应结果和耗时信息

## 故障排除

### 后端启动失败

**问题1: 数据库连接失败**
```bash
# 检查MySQL服务是否启动
systemctl status mysql  # Linux
brew services list      # macOS

# 检查数据库连接
mysql -u root -p -e "SHOW DATABASES;"
```

**问题2: Java版本不兼容**
```bash
# 检查Java版本
java -version
# 需要Java 11或更高版本
```

**问题3: 端口被占用**
```bash
# 检查8080端口占用
lsof -i :8080          # macOS/Linux
netstat -ano | findstr :8080  # Windows

# 更改配置文件中的端口号
```

### 前端连接失败

**问题1: ECONNREFUSED错误**
这通常表示后端服务没有启动，请确保：
1. 后端服务正在运行
2. 后端端口配置正确（默认8080）
3. 防火墙允许localhost连接

**问题2: 网络错误**
检查前端配置 `vite.config.ts` 中的代理配置：
```typescript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## 生产环境部署

### 后端部署
```bash
# 构建生产包
cd backend
mvn clean package -DskipTests

# 运行生产包
java -jar target/esb-gateway-1.0.0.jar \
  --spring.profiles.active=prod \
  --server.port=8080
```

### 前端部署
```bash
# 构建生产包
cd frontend
npm run build

# 部署dist目录内容到Web服务器
# (Nginx, Apache, 或静态文件托管服务)
```

## API文档

### 核心接口

1. **接口代理**
   - POST `/api/proxy/request`
   - 支持GET/POST/PUT/DELETE方法

2. **统计数据**
   - GET `/api/metrics`
   - 获取调用统计和趋势数据

3. **日志查询**
   - GET `/api/logs`
   - 支持分页和多条件查询

4. **服务管理**
   - GET `/api/services/active`
   - 获取可用服务列表

### GLM服务配置

当前配置的GLM服务：
- 服务名称：glm-service
- 目标地址：https://llmapi-pub.giihgai.com:4000
- 默认路径：/v1/chat/completions
- 超时时间：30秒

## 支持与帮助

遇到问题请：
1. 检查各服务日志文件
2. 确认网络连接正常
3. 验证数据库配置正确
4. 查看防火墙和安全组设置

## 开发环境配置建议

### IDE配置
- IntelliJ IDEA 或 Eclipse
- 配置正确的JDK版本
- 安装Lombok插件
- 配置Maven镜像仓库

### 数据库工具
- Navicat 或 DBeaver
- 配置数据库连接
- 验证表结构创建

### API测试工具
- Postman 或 Apifox
- 测试后端接口
- 验证代理功能