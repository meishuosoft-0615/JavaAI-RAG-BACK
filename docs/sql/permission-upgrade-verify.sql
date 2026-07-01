USE enterprise_rag;

SHOW TABLES LIKE 'sys_permission';
SHOW TABLES LIKE 'sys_role_permission';

SELECT permission_code, permission_name, module_code
FROM sys_permission
ORDER BY id;

SELECT r.role_code, GROUP_CONCAT(p.permission_code ORDER BY p.permission_code) AS permissions
FROM sys_role r
LEFT JOIN sys_role_permission rp
  ON rp.tenant_id = r.tenant_id
 AND rp.role_id = r.id
LEFT JOIN sys_permission p
  ON p.id = rp.permission_id
WHERE r.tenant_id = 1
GROUP BY r.role_code
ORDER BY r.role_code;

SELECT DISTINCT p.permission_code
FROM sys_user u
JOIN sys_user_role ur
  ON ur.tenant_id = u.tenant_id
 AND ur.user_id = u.id
JOIN sys_role r
  ON r.tenant_id = ur.tenant_id
 AND r.id = ur.role_id
 AND r.status = 'ENABLED'
 AND r.deleted = 0
JOIN sys_role_permission rp
  ON rp.tenant_id = r.tenant_id
 AND rp.role_id = r.id
JOIN sys_permission p
  ON p.id = rp.permission_id
WHERE u.tenant_id = 1
  AND u.username = 'admin'
  AND u.status = 'ENABLED'
  AND u.deleted = 0
ORDER BY p.permission_code;
