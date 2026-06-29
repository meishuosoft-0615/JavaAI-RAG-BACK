USE enterprise_rag;

INSERT INTO sys_tenant (id, name, code, status)
VALUES (1, '演示租户', 'demo', 'ENABLED')
ON DUPLICATE KEY UPDATE name = VALUES(name), status = VALUES(status);

INSERT INTO sys_department (id, tenant_id, parent_id, name, code, status)
VALUES (1, 1, NULL, '默认部门', 'default', 'ENABLED')
ON DUPLICATE KEY UPDATE name = VALUES(name), status = VALUES(status);

INSERT INTO sys_role (id, tenant_id, role_code, role_name, status)
VALUES
  (1, 1, 'system_admin', '系统管理员', 'ENABLED'),
  (2, 1, 'kb_admin', '知识库管理员', 'ENABLED'),
  (3, 1, 'user', '普通用户', 'ENABLED')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name), status = VALUES(status);

INSERT INTO sys_user (id, tenant_id, username, password_hash, real_name, department_id, status)
VALUES (1, 1, 'admin', '{noop}admin123', '演示管理员', 1, 'ENABLED')
ON DUPLICATE KEY UPDATE password_hash = VALUES(password_hash), real_name = VALUES(real_name), status = VALUES(status);

INSERT INTO sys_user_role (tenant_id, user_id, role_id)
VALUES
  (1, 1, 1),
  (1, 1, 2),
  (1, 1, 3)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);
