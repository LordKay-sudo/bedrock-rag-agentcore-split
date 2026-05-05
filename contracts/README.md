# Contracts

Shared shapes for the orchestration edge and both paths:

| File | Purpose |
|------|---------|
| `query-envelope.schema.json` | Request: `message`, optional `preferredPath`, optional `correlationId`. |
| `query-response.schema.json` | Response: `pathUsed`, `text`, optional `citations`. |
| `openapi.yaml` | HTTP documentation for the RAG gateway surface (mirrors Java DTOs). |
| `orchestration-openapi.yaml` | Public edge on port 8082; same request/response schemas. |

Java DTOs: `services/rag-gateway`, `services/orchestration-edge`. Python: `services/agent-runtime/src/agent_runtime/main.py`. Keep JSON field names aligned when changing contracts.
