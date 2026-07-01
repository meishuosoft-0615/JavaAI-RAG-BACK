USE enterprise_rag;

CREATE TABLE IF NOT EXISTS sys_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限主键 ID',
  permission_code VARCHAR(128) NOT NULL COMMENT '权限编码，必须和前端 permission 字符串一致',
  permission_name VARCHAR(128) NOT NULL COMMENT '权限名称，用于后台展示',
  module_code VARCHAR(64) NOT NULL COMMENT '所属模块编码，如 kb、document、index、evaluation、audit',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_sys_permission_code (permission_code),
  KEY idx_sys_permission_module (module_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统功能权限表，用于控制菜单、按钮和接口操作能力';

CREATE TABLE IF NOT EXISTS sys_role_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色权限关系主键 ID',
  tenant_id BIGINT NOT NULL COMMENT '所属租户 ID',
  role_id BIGINT NOT NULL COMMENT '角色 ID',
  permission_id BIGINT NOT NULL COMMENT '权限 ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_sys_role_permission (tenant_id, role_id, permission_id),
  KEY idx_sys_role_permission_role (tenant_id, role_id),
  KEY idx_sys_role_permission_permission (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限关系表，用于聚合当前用户系统功能权限';

INSERT INTO sys_permission (id, permission_code, permission_name, module_code)
VALUES
  (1, 'kb:manage', '知识库管理', 'kb'),
  (2, 'document:manage', '文档管理', 'document'),
  (3, 'document:read', '文档查看', 'document'),
  (4, 'index:manage', '索引治理', 'index'),
  (5, 'eval:manage', '评测管理', 'evaluation'),
  (6, 'eval:read', '评测查看', 'evaluation'),
  (7, 'audit:read', '审计日志查看', 'audit')
ON DUPLICATE KEY UPDATE
  permission_name = VALUES(permission_name),
  module_code = VALUES(module_code);

INSERT INTO sys_role_permission (tenant_id, role_id, permission_id)
SELECT 1, r.id, p.id
FROM sys_role r
JOIN sys_permission p
  ON p.permission_code IN (
    'kb:manage',
    'document:manage',
    'document:read',
    'index:manage',
    'eval:manage',
    'eval:read',
    'audit:read'
  )
WHERE r.tenant_id = 1
  AND r.role_code = 'system_admin'
ON DUPLICATE KEY UPDATE permission_id = VALUES(permission_id);

INSERT INTO sys_role_permission (tenant_id, role_id, permission_id)
SELECT 1, r.id, p.id
FROM sys_role r
JOIN sys_permission p
  ON p.permission_code IN (
    'kb:manage',
    'document:manage',
    'document:read',
    'index:manage',
    'eval:manage',
    'eval:read'
  )
WHERE r.tenant_id = 1
  AND r.role_code = 'kb_admin'
ON DUPLICATE KEY UPDATE permission_id = VALUES(permission_id);

INSERT INTO sys_role_permission (tenant_id, role_id, permission_id)
SELECT 1, r.id, p.id
FROM sys_role r
JOIN sys_permission p
  ON p.permission_code IN ('document:read', 'eval:read')
WHERE r.tenant_id = 1
  AND r.role_code = 'user'
ON DUPLICATE KEY UPDATE permission_id = VALUES(permission_id);
