#!/usr/bin/env bash
set -euo pipefail

ES_URL="${ELASTICSEARCH_URIS:-http://localhost:9200}"
INDEX_NAME="${RAG_INDEX_NAME:-kb_chunk_v1}"
ALIAS_NAME="${RAG_INDEX_ALIAS:-kb_chunk_current}"

curl -fsS -X PUT "${ES_URL}/${INDEX_NAME}" \
  -H "Content-Type: application/json" \
  -d @docs/es/kb_chunk_mapping.json

curl -fsS -X POST "${ES_URL}/_aliases" \
  -H "Content-Type: application/json" \
  -d "{\"actions\":[{\"add\":{\"index\":\"${INDEX_NAME}\",\"alias\":\"${ALIAS_NAME}\"}}]}"
