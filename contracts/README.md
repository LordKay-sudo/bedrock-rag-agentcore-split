# Contracts

Shared shapes for the orchestration edge and both paths:

| File | Purpose |
|------|---------|
| `query-envelope.schema.json` | Request: `message`, optional `preferredPath`, optional `correlationId`. |
| `query-response.schema.json` | Response: `pathUsed`, `text`, optional `citations`. |
| `openapi.yaml` | HTTP documentation for the RAG gateway surface (mirrors Java DTOs). |

Java models live in `services/rag-gateway`; Python models in `services/agent-runtime/src/agent_runtime/main.py`. Keep field names aligned when changing contracts.
