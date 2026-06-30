# 本地启动说明

## 1. 前置条件

- Docker Desktop
- JDK 17
- Maven 3.9+

当前项目使用 Spring Boot 3.x，必须确认 Maven 绑定到 JDK 17：

```powershell
java -version
mvn -version
```

如果仍显示 Java 8，需要先调整：

```powershell
$env:JAVA_HOME="你的 JDK17 安装目录"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
```

## 2. 启动基础环境

```powershell
docker compose up -d mysql redis elasticsearch minio
```

需要查看 ES 数据时再启动 Kibana：

```powershell
docker compose up -d kibana
```

## 3. 检查服务

```powershell
docker compose ps
docker exec enterprise-rag-mysql mysql -uroot -proot enterprise_rag -e "SHOW TABLES;"
curl http://localhost:9200/_cluster/health
```

开发联调用账号初始化：

```powershell
docker exec -i enterprise-rag-mysql mysql -uroot -proot enterprise_rag < docs/sql/dev-seed.sql
```

## 4. 创建 Elasticsearch 索引

PowerShell 示例：

```powershell
curl.exe -X PUT "http://localhost:9200/kb_chunk_v1" -H "Content-Type: application/json" --data-binary "@docs/es/kb_chunk_mapping.json"
curl.exe -X POST "http://localhost:9200/_aliases" -H "Content-Type: application/json" -d "{\"actions\":[{\"add\":{\"index\":\"kb_chunk_v1\",\"alias\":\"kb_chunk_current\"}}]}"
```

Git Bash / WSL 示例：

```bash
bash docs/es/create-index.sh
```

## 5. 启动后端

```powershell
mvn clean package
java -jar target/enterprise-rag-platform-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

健康检查：

```powershell
curl http://localhost:8080/api/health
```

登录验证：

```powershell
curl.exe -X POST "http://localhost:8080/api/auth/login" -H "Content-Type: application/json" -d "{\"tenantCode\":\"demo\",\"username\":\"admin\",\"password\":\"admin123\"}"
```

注销验证：

```powershell
curl.exe -X POST "http://localhost:8080/api/auth/logout"
```

Swagger / OpenAPI 文档地址：

```text
Swagger UI:   http://localhost:8080/swagger-ui.html
OpenAPI JSON: http://localhost:8080/v3/api-docs
OpenAPI YAML: http://localhost:8080/v3/api-docs.yaml
```

创建知识库空间：

```powershell
$token="登录接口返回的 accessToken"
curl.exe -X POST "http://localhost:8080/api/kb/spaces" -H "Authorization: Bearer $token" -H "Content-Type: application/json" -d "{\"name\":\"HR 制度知识库\",\"description\":\"员工制度、流程和报销政策\"}"
curl.exe "http://localhost:8080/api/kb/spaces?pageNo=1&pageSize=20" -H "Authorization: Bearer $token"
```

## 6. 回滚本地数据库表

仅用于本地开发清理，会删除当前库内本项目所有业务表：

```powershell
docker exec -i enterprise-rag-mysql mysql -uroot -proot enterprise_rag < docs/sql/rollback.sql
```
