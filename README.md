# Mall 电商平台

一个按「单体跑通 → 工程化 → 缓存 → 异步 → 微服务 → 分库分表」路线演进的 Java 电商项目。

当前进度：**Day 1 —— 工程骨架 + 通用基础设施**。

---

## 技术栈

| 分类 | 选型 | 版本 |
|---|---|---|
| JDK | OpenJDK | 21 (LTS) |
| 构建 | Maven 多模块 | 3.8+ |
| 框架 | Spring Boot | 3.3.5 |
| Web | Spring MVC + Tomcat | 随 Boot |
| 校验 | Jakarta Validation | 随 Boot |
| 工具 | Lombok | 随 Boot 管理 |
| 数据库 | MySQL | 8.0.36（Docker） |
| 缓存 | Redis | 7.2（Docker） |
| 后续 | Spring Cloud / Alibaba、RocketMQ、ShardingSphere、Sentinel | M4~M7 引入 |

---

## 目录结构

```
mall/
├── pom.xml                     # 父 POM（依赖版本统一管理）
├── docker-compose.yml          # 本地基础设施：MySQL + Redis
├── .env.example                # 环境变量样例
├── docs/
│   ├── day1.md                 # Day 1 验收清单
│   └── day1-tech-design.md     # Day 1 技术设计文档
├── mall-common/                # 通用层（被所有业务模块依赖）
│   └── com.mall.common
│       ├── api/                # Result / ResultCode / PageResult
│       ├── exception/          # BizException / GlobalExceptionHandler
│       ├── web/                # TraceIdFilter / TraceContext / 自动装配
│       └── util/               # IdUtils
└── mall-product/               # 商品服务（第一个业务模块）
    ├── com.mall.product
    │   ├── ProductApplication.java
    │   ├── controller/PingController.java
    │   └── vo/PingVO.java
    └── resources/
        ├── application.yml
        ├── application-dev.yml
        └── logback-spring.xml
```

---

## 文档索引

| 文档 | 内容 |
|---|---|
| [Day 1 技术设计文档](docs/day1-tech-design.md) | 架构设计、统一响应/错误码/异常/traceId 方案、接口契约、配置、测试验收 |
| [Day 1 验收清单](docs/day1.md) | 逐步验收命令与预期结果、排障表 |

---

## 环境要求

- JDK 21（`java -version` 确认）
- Maven 3.8+（`mvn -v` 确认）
- Docker Desktop（Windows 需启用 WSL2 后端）

---

## 快速开始

### 1. 启动基础设施

```bash
cd E:\deepseek\mall
docker compose up -d
docker compose ps          # 等 mysql 变成 healthy
```

首次启动 MySQL 约需 30~60 秒。默认账号：

| 组件 | 地址 | 账号 / 密码 |
|---|---|---|
| MySQL | `127.0.0.1:3306` | `root` / `mall123456`，库名 `mall` |
| Redis | `127.0.0.1:6379` | 密码 `mall123456` |

如需改密码或端口：复制 `.env.example` 为 `.env` 后修改。

### 2. 编译打包

```bash
mvn clean install -DskipTests
```

### 3. 启动商品服务

```bash
mvn -pl mall-product -am spring-boot:run
```

### 4. 验证

```bash
curl.exe http://localhost:8081/ping
```

期望返回（`traceId` 每次不同）：

```json
{
  "code": 0,
  "message": "成功",
  "data": {
    "service": "mall-product",
    "status": "UP",
    "serverTime": "2026-10-25T22:30:00.123+08:00",
    "javaVersion": "21.0.x",
    "profile": "dev"
  },
  "traceId": "18f3a1b2c3d_9a1f2b3c4d5e6f70",
  "timestamp": 1729871234567,
  "success": true
}
```

完整验收步骤见 [docs/day1.md](docs/day1.md)。

---

## 工程约定

### 统一响应

所有接口返回 `Result<T>`，**业务失败用 HTTP 200 + 业务码**，只有参数错误/系统错误才用 4xx/5xx：

| 场景 | HTTP | code |
|---|---|---|
| 成功 | 200 | 0 |
| 业务异常（库存不足等） | 200 | 40xxx / 30xxx … |
| 参数校验失败 | 400 | 10000 |
| 未登录 | 401 | 10001 |
| 无权限 | 403 | 10003 |
| 资源不存在 | 404 | 10004 |
| 系统异常 | 500 | 99999 |

错误码按业务段划分，见 `ResultCode`。

### 异常

- 可预期的业务错误：`throw new BizException(ResultCode.XXX)` 或带自定义 message
- 不可预期：直接抛出，由兜底处理返回 500 并记录完整堆栈
- **禁止**在 Controller 里 try-catch 后返回 `Result.fail`，统一走全局处理

### traceId

- 每次请求自动生成，或在请求头 `X-Trace-Id` 中透传
- 响应头回写 `X-Trace-Id`，响应体带 `traceId`
- 日志模板已包含 `[%X{traceId}]`，排查问题时按 traceId 一搜即得
- 异步线程需用 `TraceContext.wrap(...)` 手动透传

### 日志

- 路径：`./logs/mall-product.log`，错误单独写 `./logs/mall-product-error.log`
- 按天 + 100MB 滚动，保留 15 天
- 生产环境建议把 `LOG_PATH` 指向挂载卷

---

## 常用命令

```bash
# 只编译某个模块（含依赖）
mvn -pl mall-product -am clean package

# 跑测试
mvn test

# 跳过测试打包
mvn clean package -DskipTests

# 停止基础设施（保留数据）
docker compose down

# 清空数据重来
docker compose down -v

# 查看 MySQL 启动日志
docker compose logs -f mysql
```

---

## 常见问题

**1. 3306 / 6379 端口被占用**

本机已装 MySQL/Redis 时，改 `.env` 里的 `MYSQL_PORT` / `REDIS_PORT`，或先停掉本机服务。

**2. `docker compose up` 卡在 mysql**

首次初始化数据目录较慢，`docker compose logs -f mysql` 观察；确认 Docker Desktop 分配的磁盘空间充足。

**3. Maven 下载依赖慢**

在 `~/.m2/settings.xml` 配置阿里云镜像：

```xml
<mirror>
  <id>aliyun</id>
  <mirrorOf>central</mirrorOf>
  <url>https://maven.aliyun.com/repository/public</url>
</mirror>
```

**4. 启动报 `Unsupported class file major version`**

说明 Maven 用的不是 JDK 21。检查 `mvn -v` 输出的 Java version，并设置 `JAVA_HOME` 指向 JDK 21。

本机现状（Windows）：JDK 21 装在 `C:\Program Files\Java\jdk-21.0.12`，但 `JAVA_HOME` 仍指向 JDK 8，
直接跑 `mvn` 会用 Java 8 编译并失败。两种处理方式：

```powershell
# 方式一：只在当前窗口生效
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.12"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

# 方式二：永久写入用户环境变量（需重开终端）
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-21.0.12", "User")
```

设置后用 `mvn -v` 确认输出 `Java version: 21.0.12`。

**5. `/ping` 返回 404**

确认启动的是 `mall-product` 模块，端口 8081 未被占用。

---

## 演进路线

| 阶段 | 内容 | 状态 |
|---|---|---|
| Day 1 | 工程骨架 + 统一响应/异常/traceId | ✅ 已完成 |
| Day 2 | 商品/库存建表 + Flyway + MyBatis-Plus + 商品接口 | ⬜ |
| Day 3 | 用户注册登录（JWT）+ 购物车 | ⬜ |
| Week 2+ | 下单/支付闭环、缓存、MQ、微服务拆分、分库分表 | ⬜ |
