USE enterprise_rag;

SHOW TABLES;

SELECT table_name, table_rows
FROM information_schema.tables
WHERE table_schema = 'enterprise_rag'
ORDER BY table_name;

EXPLAIN SELECT *
FROM kb_document
WHERE tenant_id = 1
  AND space_id = 100
  AND status = 'INDEXED'
  AND deleted = 0
ORDER BY updated_at DESC
LIMIT 20;

EXPLAIN SELECT *
FROM kb_index_task
WHERE status = 'PENDING'
  AND retry_count < max_retry_count
ORDER BY created_at ASC
LIMIT 10;

EXPLAIN SELECT *
FROM chat_session
WHERE tenant_id = 1
  AND user_id = 88
  AND deleted = 0
ORDER BY updated_at DESC
LIMIT 20;
