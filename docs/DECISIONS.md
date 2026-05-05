# Architecture decisions

Short log of significant choices. Add entries as the design hardens.

| ID | Date | Decision | Rationale |
|----|------|----------|-----------|
| ADR-001 | — | Split RAG and agent into separate deployables | Different scaling, IAM, and blast-radius profiles; keeps document grounding separate from tool execution. |
| ADR-002 | 2026-05 | Ship stub implementations before Bedrock/KB wiring | Unblocks contract tests, CI, and container layout; swap `StubRagQueryService` and FastAPI handlers without changing JSON contracts. |
| ADR-003 | 2026-05 | FastAPI for agent-runtime local + Docker image | AgentCore still expects a container; FastAPI gives health + `/v1/invoke` for integration tests until `BedrockAgentCoreApp` replaces the ASGI app. |
| ADR-004 | 2026-05 | Dedicated **orchestration-edge** Spring service | Keeps routing and downstream HTTP concerns out of rag-gateway and agent-runtime; clients call one host (`:8082`) in dev and Compose. |
| ADR-005 | 2026-05 | **auto** routing uses keyword heuristics | Cheap default until a classifier, policy table, or LLM router exists; explicit `preferredPath` always wins. |
| ADR-006 | 2026-05 | Add edge hardening controls before KB integration | Enforces rate limits, request sanitation, and safer external error responses before introducing real LLM traffic. |
| ADR-007 | 2026-05 | Use in-memory retry/circuit/cache/budget guards first | Keeps controls simple and testable in local dev; future versions can externalize state to Redis and metrics stores. |
