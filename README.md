# 企业 RAG 智能知识库平台后端

本项目依据《权限安全的企业 RAG 智能知识库平台产品实施方案》逐步搭建，目标是实现企业知识库从文档上传、解析、切片、向量索引、权限过滤到可追溯问答的后端能力。

## 当前进度

- 第 1 步：Spring Boot 后端基础骨架。
- 第 2 步：Docker Compose 基础环境、MySQL 初版表结构、Elasticsearch chunk mapping、本地启动文档。
- 第 3 步：用户登录、JWT、当前用户权限上下文。
- 第 4 步：知识库空间创建、可见列表、状态管理和空间 ACL。
- 注释规范：数据库表/字段、核心 Java 类/字段默认使用中文注释，规范见 [docs/development/commenting-guidelines.md](docs/development/commenting-guidelines.md)。

## 技术栈

- Java 17
- Spring Boot 3.x
- MySQL 8
- Redis 7
- Elasticsearch 8
- MyBatis-Plus
- Apache Tika
- MinIO 或本地文件存储
- Maven

## 本地启动

第 2 步已经补齐 Docker Compose、数据库脚本和 ES mapping。启动前先确认本机具备：

- JDK 17
- Maven 3.9+

基础构建命令：

```bash
mvn clean package
```

本地运行命令：

```bash
java -jar target/enterprise-rag-platform-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

## 基础环境

```bash
docker compose up -d mysql redis elasticsearch minio
```

本地启动细节见 [docs/deploy/local-start.md](docs/deploy/local-start.md)。
