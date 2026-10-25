# Day 1 验收清单

> 目标：工程能编译、能启动、`/ping` 通，且统一响应 / 异常处理 / traceId 三条主干链路全部验证通过。

---

## 一、交付内容

| # | 任务 | 产出 | 状态 |
|---|---|---|---|
| 1 | 本地基础设施 | `docker-compose.yml`（MySQL 8.0.36 + Redis 7.2） | ✅ |
| 2 | 父 POM | `pom.xml`（Spring Boot 3.3.5、JDK 21、模块聚合、版本管理） | ✅ |
| 3 | 通用模块 | `mall-common`：统一响应、错误码、业务异常、全局异常、traceId | ✅ |
| 4 | 业务模块骨架 | `mall-product`：启动类、`/ping`、配置、日志 | ✅ |
| 5 | 自动装配 | `AutoConfiguration.imports`：通用能力零配置生效 | ✅ |
| 6 | 测试 | `PingControllerTest`：5 个用例覆盖主干链路 | ✅ |

---

## 二、验收步骤

### Step 1：基础设施

```bash
cd E:\deepseek\mall
docker compose up -d
docker compose ps
```

**通过标准**：`mall-mysql` 的 STATUS 显示 `(healthy)`，`mall-redis` 显示 `(healthy)`。

```bash
# 验证 MySQL
docker exec -it mall-mysql mysql -uroot -pmall123456 -e "show databases;"

# 验证 Redis
docker exec -it mall-redis redis-cli -a mall123456 ping
# 期望输出：PONG
```

### Step 2：编译

```bash
mvn clean install -DskipTests
```

**通过标准**：`BUILD SUCCESS`，本地仓库生成 `com/mall/mall-common/1.0.0-SNAPSHOT` 与 `mall-product`。

### Step 3：测试

```bash
mvn test
```

**通过标准**：`Tests run: 5, Failures: 0, Errors: 0`。

> 若首次运行报 `No tests were executed`，检查 `mall-product/pom.xml` 是否引入了 `spring-boot-starter-test`。

### Step 4：启动

```bash
mvn -pl mall-product -am spring-boot:run
```

**通过标准**：控制台出现 `Tomcat started on port 8081` 与 `Started ProductApplication in x.x seconds`。

### Step 5：接口验收

| # | 请求 | 期望 |
|---|---|---|
| 1 | `curl.exe http://localhost:8081/ping` | HTTP 200，`code=0`，`data.service=mall-product` |
| 2 | `curl.exe -i http://localhost:8081/ping` | 响应头包含 `X-Trace-Id` |
| 3 | `curl.exe -H "X-Trace-Id: trace-12345678" http://localhost:8081/ping` | 响应头与响应体 traceId 均为 `trace-12345678` |
| 4 | `curl.exe http://localhost:8081/ping/biz-error` | HTTP 200，`code=40001` |
| 5 | `curl.exe "http://localhost:8081/ping/validate?name="` | HTTP 400，`code=10000` |
| 6 | `curl.exe http://localhost:8081/ping/boom` | HTTP 500，`code=99999`，无堆栈信息 |
| 7 | `curl.exe http://localhost:8081/actuator/health` | `{"status":"UP"}` |

PowerShell 若 `curl` 别名不识别，统一用 `curl.exe`。

### Step 6：日志验收

```bash
type logs\mall-product.log
```

**通过标准**：每行包含 `[traceId]`，且能看到刚才请求的访问日志。用同一个 traceId 搜索，能串起一次请求的全部日志。

---

## 三、代码自检点

- [ ] `Result` / `ResultCode` 在 `mall-common`，业务模块不重复定义
- [ ] 业务异常只抛 `BizException`，Controller 无 try-catch
- [ ] 所有响应都经过 `GlobalExceptionHandler` 或 `Result.ok()`
- [ ] `TraceIdFilter` 的 `finally` 中清理了 MDC（防止线程池串号）
- [ ] `mall-common` 通过自动装配注入，`ProductApplication` 无 `@ComponentScan` 硬编码
- [ ] `application-dev.yml` 中的数据库配置保持注释状态（Day 2 再打开）

---

## 四、Day 1 已知限制

- **未接入数据库**：MySQL 容器已启动但应用未连接，Day 2 引入 MyBatis-Plus + Flyway 后接入。
- **未接入 Redis**：同上，Day 2 或 Day 3 接入。
- **无鉴权**：JWT 登录在 Day 3 完成。
- **无网关**：M5 微服务阶段引入。

---

## 五、Day 2 预告

1. 设计商品域表结构（`category` / `product` / `sku` / `inventory`）
2. 引入 MyBatis-Plus + Flyway，实现自动建表与版本管理
3. 实现商品列表（分页）、商品详情（含 SKU 列表）接口
4. 引入 Redis 缓存商品详情，验证缓存命中
5. 造 20 条测试数据，用 Swagger 或 curl 调通

---

## 六、排障

| 现象 | 原因 | 处理 |
|---|---|---|
| 编译报 `invalid target release: 21` | Maven 用的 JDK < 21 | 设置 `JAVA_HOME` 指向 JDK 21 |
| 编译报找不到 `mall-common` | 未先 install | 先执行 `mvn clean install -DskipTests` |
| `/ping` 404 | 启动的是别的模块 / 端口冲突 | 看启动日志端口，确认 8081 未被占用 |
| 启动报 `Port 8081 was already in use` | 端口占用 | `netstat -ano | findstr 8081` 找到进程并结束 |
| 中文日志乱码 | 控制台编码 | PowerShell 执行 `chcp 65001`，文件日志本身是 UTF-8 |
| `docker compose` 报 version | 命令写成 `docker-compose` | Docker Desktop 新版用 `docker compose` |
