CREATE DATABASE IF NOT EXISTS enterprise_rag
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE enterprise_rag;

CREATE TABLE IF NOT EXISTS sys_tenant (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '租户主键 ID',
  name VARCHAR(128) NOT NULL COMMENT '租户名称，用于后台展示',
  code VARCHAR(64) NOT NULL COMMENT '租户编码，登录时用于定位租户',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '租户状态：ENABLED 启用，DISABLED 停用',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记：0 未删除，1 已删除',
  UNIQUE KEY uk_sys_tenant_code (code),
  KEY idx_sys_tenant_status (status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统租户表，支持企业多租户隔离';

CREATE TABLE IF NOT EXISTS sys_department (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '部门主键 ID',
  tenant_id BIGINT NOT NULL COMMENT '所属租户 ID',
  parent_id BIGINT NULL COMMENT '上级部门 ID，根部门为空',
  name VARCHAR(128) NOT NULL COMMENT '部门名称',
  code VARCHAR(64) NOT NULL COMMENT '部门编码，同一租户内唯一',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '部门状态：ENABLED 启用，DISABLED 停用',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记：0 未删除，1 已删除',
  UNIQUE KEY uk_sys_department_code (tenant_id, code),
  KEY idx_sys_department_parent (tenant_id, parent_id, deleted),
  KEY idx_sys_department_status (tenant_id, status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统部门表，用于知识库和文档的部门级权限控制';

CREATE TABLE IF NOT EXISTS sys_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色主键 ID',
  tenant_id BIGINT NOT NULL COMMENT '所属租户 ID',
  role_code VARCHAR(64) NOT NULL COMMENT '角色编码，如 system_admin、kb_admin、user',
  role_name VARCHAR(128) NOT NULL COMMENT '角色名称，用于后台展示',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '角色状态：ENABLED 启用，DISABLED 停用',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记：0 未删除，1 已删除',
  UNIQUE KEY uk_sys_role_code (tenant_id, role_code),
  KEY idx_sys_role_status (tenant_id, status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统角色表，用于菜单权限和知识访问权限分组';

CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户主键 ID',
  tenant_id BIGINT NOT NULL COMMENT '所属租户 ID',
  username VARCHAR(64) NOT NULL COMMENT '登录用户名，同一租户内唯一',
  password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希，禁止保存明文密码',
  real_name VARCHAR(64) NOT NULL COMMENT '用户真实姓名',
  department_id BIGINT NULL COMMENT '用户所属部门 ID，用于部门级权限过滤',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '用户状态：ENABLED 启用，DISABLED 停用',
  last_login_at DATETIME NULL COMMENT '最近登录时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记：0 未删除，1 已删除',
  UNIQUE KEY uk_sys_user_username (tenant_id, username),
  KEY idx_sys_user_department (tenant_id, department_id, deleted),
  KEY idx_sys_user_status (tenant_id, status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户表，登录认证和权限上下文的数据来源';

CREATE TABLE IF NOT EXISTS sys_user_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户角色关系主键 ID',
  tenant_id BIGINT NOT NULL COMMENT '所属租户 ID',
  user_id BIGINT NOT NULL COMMENT '用户 ID',
  role_id BIGINT NOT NULL COMMENT '角色 ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_sys_user_role (tenant_id, user_id, role_id),
  KEY idx_sys_user_role_role (tenant_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关系表，用于构建登录后的角色权限上下文';

CREATE TABLE IF NOT EXISTS kb_space (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '知识库空间主键 ID',
  tenant_id BIGINT NOT NULL COMMENT '所属租户 ID',
  name VARCHAR(128) NOT NULL COMMENT '知识库空间名称',
  description VARCHAR(512) NULL COMMENT '知识库空间描述',
  owner_id BIGINT NOT NULL COMMENT '空间负责人用户 ID，默认拥有管理权限',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '空间状态：ENABLED 启用，DISABLED 停用',
  created_by BIGINT NOT NULL COMMENT '创建人用户 ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记：0 未删除，1 已删除',
  KEY idx_kb_space_tenant_status (tenant_id, status, deleted),
  KEY idx_kb_space_owner (tenant_id, owner_id, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='知识库空间表，是文档、索引和问答的权限边界';

CREATE TABLE IF NOT EXISTS kb_space_acl (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '空间权限主键 ID',
  tenant_id BIGINT NOT NULL COMMENT '所属租户 ID',
  space_id BIGINT NOT NULL COMMENT '知识库空间 ID',
  department_id BIGINT NULL COMMENT '授权部门 ID，为空表示不是部门授权',
  role_id BIGINT NULL COMMENT '授权角色 ID，为空表示不是角色授权',
  user_id BIGINT NULL COMMENT '授权用户 ID，为空表示不是用户授权',
  permission VARCHAR(32) NOT NULL DEFAULT 'READ' COMMENT '空间权限：READ 可访问，MANAGE 可管理',
  created_by BIGINT NOT NULL COMMENT '创建人用户 ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_kb_space_acl_space (tenant_id, space_id),
  KEY idx_kb_space_acl_department (tenant_id, department_id, permission),
  KEY idx_kb_space_acl_role (tenant_id, role_id, permission),
  KEY idx_kb_space_acl_user (tenant_id, user_id, permission)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='知识库空间 ACL 表，控制用户能访问或管理哪些空间';

CREATE TABLE IF NOT EXISTS kb_document (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文档主键 ID',
  tenant_id BIGINT NOT NULL COMMENT '所属租户 ID',
  space_id BIGINT NOT NULL COMMENT '所属知识库空间 ID',
  title VARCHAR(255) NOT NULL COMMENT '文档标题，用于检索和引用展示',
  file_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
  file_type VARCHAR(32) NOT NULL COMMENT '文件类型，如 PDF、WORD、MARKDOWN、TXT',
  file_url VARCHAR(512) NOT NULL COMMENT '文件存储地址，本地路径或对象存储 URL',
  checksum VARCHAR(128) NOT NULL COMMENT '文件内容校验值，用于判断重复上传或版本变化',
  current_version INT NOT NULL DEFAULT 1 COMMENT '当前文档版本号',
  status VARCHAR(32) NOT NULL DEFAULT 'UPLOADED' COMMENT '文档状态：UPLOADED、PARSING、INDEXED、FAILED',
  failure_reason VARCHAR(1024) NULL COMMENT '解析或索引失败原因',
  created_by BIGINT NOT NULL COMMENT '上传人用户 ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记：0 未删除，1 已删除',
  KEY idx_kb_document_space_status (tenant_id, space_id, status, deleted),
  KEY idx_kb_document_checksum (tenant_id, space_id, checksum),
  KEY idx_kb_document_created (tenant_id, created_by, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='知识库文档表，保存上传文档的元数据和当前状态';

CREATE TABLE IF NOT EXISTS kb_document_version (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文档版本主键 ID',
  tenant_id BIGINT NOT NULL COMMENT '所属租户 ID',
  document_id BIGINT NOT NULL COMMENT '文档 ID',
  version INT NOT NULL COMMENT '版本号，同一文档内递增',
  file_url VARCHAR(512) NOT NULL COMMENT '该版本文件存储地址',
  checksum VARCHAR(128) NOT NULL COMMENT '该版本文件内容校验值',
  status VARCHAR(32) NOT NULL DEFAULT 'DRAFT' COMMENT '版本状态：DRAFT 草稿，ACTIVE 生效，ARCHIVED 归档',
  indexed_at DATETIME NULL COMMENT '该版本完成索引构建的时间',
  created_by BIGINT NOT NULL COMMENT '创建人用户 ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_kb_document_version (tenant_id, document_id, version),
  KEY idx_kb_document_version_status (tenant_id, document_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文档版本表，支持文档更新、索引切换和版本回滚';

CREATE TABLE IF NOT EXISTS kb_document_acl (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文档权限主键 ID',
  tenant_id BIGINT NOT NULL COMMENT '所属租户 ID',
  document_id BIGINT NOT NULL COMMENT '文档 ID',
  department_id BIGINT NULL COMMENT '授权部门 ID，为空表示不是部门授权',
  role_id BIGINT NULL COMMENT '授权角色 ID，为空表示不是角色授权',
  user_id BIGINT NULL COMMENT '授权用户 ID，为空表示不是用户授权',
  permission VARCHAR(32) NOT NULL DEFAULT 'READ' COMMENT '文档权限：READ 可读，MANAGE 可管理',
  created_by BIGINT NOT NULL COMMENT '创建人用户 ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_kb_document_acl_document (tenant_id, document_id),
  KEY idx_kb_document_acl_department (tenant_id, department_id, permission),
  KEY idx_kb_document_acl_role (tenant_id, role_id, permission),
  KEY idx_kb_document_acl_user (tenant_id, user_id, permission)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文档 ACL 表，控制检索阶段可命中的文档范围';

CREATE TABLE IF NOT EXISTS kb_chunk (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文档切片主键 ID',
  tenant_id BIGINT NOT NULL COMMENT '所属租户 ID',
  space_id BIGINT NOT NULL COMMENT '所属知识库空间 ID',
  document_id BIGINT NOT NULL COMMENT '来源文档 ID',
  document_version INT NOT NULL COMMENT '来源文档版本号',
  chunk_no INT NOT NULL COMMENT '切片序号，同一文档版本内递增',
  title VARCHAR(255) NOT NULL COMMENT '来源文档标题',
  section_title VARCHAR(255) NULL COMMENT '切片所属章节标题',
  page_no INT NULL COMMENT '来源页码，无法识别时为空',
  content MEDIUMTEXT NOT NULL COMMENT '切片原文内容，用于召回、Prompt 和引用展示',
  token_count INT NOT NULL DEFAULT 0 COMMENT '估算 token 数，用于控制 Prompt 长度',
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '切片状态：ACTIVE 生效，ARCHIVED 归档',
  es_doc_id VARCHAR(128) NULL COMMENT '写入 Elasticsearch 后的文档 ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_kb_chunk_no (tenant_id, document_id, document_version, chunk_no),
  KEY idx_kb_chunk_doc_status (tenant_id, document_id, document_version, status),
  KEY idx_kb_chunk_space_status (tenant_id, space_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文档切片表，保存 RAG 检索和引用追溯的最小知识单元';

CREATE TABLE IF NOT EXISTS kb_index_task (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '索引任务主键 ID',
  tenant_id BIGINT NOT NULL COMMENT '所属租户 ID',
  space_id BIGINT NOT NULL COMMENT '所属知识库空间 ID',
  document_id BIGINT NULL COMMENT '文档 ID，空间级重建任务可为空',
  task_type VARCHAR(32) NOT NULL COMMENT '任务类型：DOCUMENT_INDEX 文档索引，SPACE_REBUILD 空间重建',
  status VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '任务状态：PENDING、RUNNING、SUCCESS、FAILED',
  retry_count INT NOT NULL DEFAULT 0 COMMENT '已重试次数',
  max_retry_count INT NOT NULL DEFAULT 3 COMMENT '最大自动重试次数',
  error_message VARCHAR(2048) NULL COMMENT '任务失败错误信息',
  started_at DATETIME NULL COMMENT '任务开始时间',
  finished_at DATETIME NULL COMMENT '任务结束时间',
  created_by BIGINT NOT NULL COMMENT '创建人用户 ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  KEY idx_kb_index_task_scan (status, retry_count, created_at),
  KEY idx_kb_index_task_document (tenant_id, document_id, created_at),
  KEY idx_kb_index_task_space (tenant_id, space_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='索引任务表，管理解析、切片、向量化和 ES 写入任务';

CREATE TABLE IF NOT EXISTS chat_session (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会话主键 ID',
  tenant_id BIGINT NOT NULL COMMENT '所属租户 ID',
  space_id BIGINT NOT NULL COMMENT '问答所属知识库空间 ID',
  user_id BIGINT NOT NULL COMMENT '发起会话的用户 ID',
  title VARCHAR(255) NOT NULL COMMENT '会话标题，默认可由首个问题生成',
  summary TEXT NULL COMMENT '会话摘要，用于多轮追问上下文压缩',
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '会话状态：ACTIVE 正常，ARCHIVED 归档',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记：0 未删除，1 已删除',
  KEY idx_chat_session_user (tenant_id, user_id, updated_at, deleted),
  KEY idx_chat_session_space (tenant_id, space_id, updated_at, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='问答会话表，保存用户与知识库空间的多轮对话';

CREATE TABLE IF NOT EXISTS chat_message (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息主键 ID',
  tenant_id BIGINT NOT NULL COMMENT '所属租户 ID',
  session_id BIGINT NOT NULL COMMENT '所属会话 ID',
  request_id VARCHAR(64) NOT NULL COMMENT '请求唯一 ID，用于日志追踪和问题排查',
  role VARCHAR(32) NOT NULL COMMENT '消息角色：USER 用户，ASSISTANT 模型',
  content MEDIUMTEXT NOT NULL COMMENT '消息内容',
  token_count INT NOT NULL DEFAULT 0 COMMENT '消息估算 token 数',
  model_provider VARCHAR(64) NULL COMMENT '模型供应商，如 openai、azure、local',
  model_name VARCHAR(128) NULL COMMENT '模型名称',
  latency_ms BIGINT NULL COMMENT '模型调用耗时，单位毫秒',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_chat_message_session (tenant_id, session_id, id),
  KEY idx_chat_message_request (tenant_id, request_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='问答消息表，保存用户问题和模型回答';

CREATE TABLE IF NOT EXISTS chat_citation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '引用主键 ID',
  tenant_id BIGINT NOT NULL COMMENT '所属租户 ID',
  message_id BIGINT NOT NULL COMMENT '关联的模型回答消息 ID',
  chunk_id BIGINT NOT NULL COMMENT '被引用的切片 ID',
  document_id BIGINT NOT NULL COMMENT '被引用的文档 ID',
  document_version INT NOT NULL COMMENT '被引用的文档版本号',
  title VARCHAR(255) NOT NULL COMMENT '引用文档标题',
  section_title VARCHAR(255) NULL COMMENT '引用章节标题',
  page_no INT NULL COMMENT '引用页码',
  snippet VARCHAR(2000) NOT NULL COMMENT '引用片段摘要',
  score DECIMAL(8,6) NULL COMMENT '检索相关性得分',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_chat_citation_message (tenant_id, message_id),
  KEY idx_chat_citation_document (tenant_id, document_id, document_version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='回答引用表，记录答案依据的文档、章节、页码和片段';

CREATE TABLE IF NOT EXISTS qa_feedback (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '反馈主键 ID',
  tenant_id BIGINT NOT NULL COMMENT '所属租户 ID',
  message_id BIGINT NOT NULL COMMENT '被反馈的回答消息 ID',
  user_id BIGINT NOT NULL COMMENT '反馈用户 ID',
  feedback_type VARCHAR(32) NOT NULL COMMENT '反馈类型：LIKE 点赞，DISLIKE 点踩，BADCASE 问题样本',
  comment VARCHAR(1000) NULL COMMENT '用户反馈说明',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_qa_feedback_user_message (tenant_id, message_id, user_id),
  KEY idx_qa_feedback_type (tenant_id, feedback_type, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='问答反馈表，用于点赞点踩和 badcase 质量闭环';

CREATE TABLE IF NOT EXISTS audit_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '审计日志主键 ID',
  tenant_id BIGINT NOT NULL COMMENT '所属租户 ID',
  user_id BIGINT NULL COMMENT '操作用户 ID，系统任务可为空',
  request_id VARCHAR(64) NOT NULL COMMENT '请求唯一 ID，用于串联访问日志和业务日志',
  action VARCHAR(64) NOT NULL COMMENT '操作类型，如 LOGIN、UPLOAD_DOCUMENT、CHAT',
  resource_type VARCHAR(64) NOT NULL COMMENT '资源类型，如 SPACE、DOCUMENT、CHAT_MESSAGE',
  resource_id BIGINT NULL COMMENT '资源 ID',
  detail JSON NULL COMMENT '审计详情 JSON，记录命中文档、权限上下文等扩展信息',
  ip VARCHAR(64) NULL COMMENT '客户端 IP',
  user_agent VARCHAR(512) NULL COMMENT '客户端 User-Agent',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_audit_log_user_time (tenant_id, user_id, created_at),
  KEY idx_audit_log_resource (tenant_id, resource_type, resource_id, created_at),
  KEY idx_audit_log_request (tenant_id, request_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='审计日志表，记录问答、文档访问和管理操作以满足合规追溯';

CREATE TABLE IF NOT EXISTS eval_case (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评测用例主键 ID',
  tenant_id BIGINT NOT NULL COMMENT '所属租户 ID',
  space_id BIGINT NOT NULL COMMENT '评测所属知识库空间 ID',
  question VARCHAR(1000) NOT NULL COMMENT '评测问题',
  expected_answer TEXT NULL COMMENT '期望答案或答案要点',
  expected_document_id BIGINT NULL COMMENT '期望命中的文档 ID',
  expected_section_title VARCHAR(255) NULL COMMENT '期望命中的章节标题',
  tags VARCHAR(255) NULL COMMENT '评测标签，如 HR、法务、无答案',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT '用例状态：ENABLED 启用，DISABLED 停用',
  created_by BIGINT NOT NULL COMMENT '创建人用户 ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记：0 未删除，1 已删除',
  KEY idx_eval_case_space_status (tenant_id, space_id, status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='RAG 评测用例表，用于验证召回率、引用准确率和拒答能力';

CREATE TABLE IF NOT EXISTS eval_run (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评测运行主键 ID',
  tenant_id BIGINT NOT NULL COMMENT '所属租户 ID',
  space_id BIGINT NOT NULL COMMENT '评测所属知识库空间 ID',
  status VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '运行状态：PENDING、RUNNING、SUCCESS、FAILED',
  recall_at5 DECIMAL(8,6) NULL COMMENT 'Recall@5 指标',
  citation_accuracy DECIMAL(8,6) NULL COMMENT '引用准确率',
  no_answer_rate DECIMAL(8,6) NULL COMMENT '无答案率',
  hallucination_rate DECIMAL(8,6) NULL COMMENT '幻觉率',
  started_at DATETIME NULL COMMENT '评测开始时间',
  finished_at DATETIME NULL COMMENT '评测结束时间',
  created_by BIGINT NOT NULL COMMENT '创建人用户 ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_eval_run_space_time (tenant_id, space_id, created_at),
  KEY idx_eval_run_status (tenant_id, status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='RAG 评测运行表，保存一次批量评测的整体指标';

CREATE TABLE IF NOT EXISTS eval_result (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评测结果主键 ID',
  tenant_id BIGINT NOT NULL COMMENT '所属租户 ID',
  run_id BIGINT NOT NULL COMMENT '评测运行 ID',
  case_id BIGINT NOT NULL COMMENT '评测用例 ID',
  question VARCHAR(1000) NOT NULL COMMENT '本次执行的问题快照',
  answer MEDIUMTEXT NULL COMMENT '模型回答内容',
  retrieved_chunk_ids JSON NULL COMMENT '检索命中的 chunk ID 列表 JSON',
  cited_chunk_ids JSON NULL COMMENT '回答引用的 chunk ID 列表 JSON',
  recall_hit TINYINT NULL COMMENT '是否命中期望文档或切片：1 是，0 否',
  citation_supported TINYINT NULL COMMENT '引用是否支持答案：1 是，0 否',
  no_answer TINYINT NULL COMMENT '是否拒答或无答案：1 是，0 否',
  hallucination TINYINT NULL COMMENT '是否存在明显幻觉：1 是，0 否',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_eval_result_run (tenant_id, run_id),
  KEY idx_eval_result_case (tenant_id, case_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='RAG 评测明细表，保存每个用例的检索、回答和指标判定';
