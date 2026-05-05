# Bedrock RAG + AgentCore split

**Dual-path GenAI architecture** on AWS: **retrieval-grounded answers** (Knowledge Bases for Amazon Bedrock + RAG) versus **tool-using / orchestrated behavior** (Amazon Bedrock AgentCore Runtime). A single orchestration boundary chooses which path to use—or how to combine them—so latency, cost, and correctness stay explicit.

## Scope

- **RAG path**: document-grounded answers (policies, specs, runbooks) via retrieve-then-generate.
- **Agent path**: tools, multi-step behavior, and isolation from the host API process (AgentCore Runtime).

Repository layout: **service contracts**, **documented tradeoffs**, and **infra as code** instead of a single notebook.

## Repository layout

| Path | Responsibility |
|------|----------------|
| `services/orchestration-edge` | **Entry** `POST /v1/query`: routes to RAG or agent from `preferredPath` + **auto** heuristics. **Spring Boot**, port **8082**. |
| `services/rag-gateway` | API that owns **retrieve-then-generate** (KB / vectors / Bedrock Converse). Intended stack: **Spring Boot**. |
| `services/agent-runtime` | **Containerized agent** for AgentCore (tools, session behavior). Target: Python + `BedrockAgentCoreApp` pattern per AWS AgentCore docs. |
| `contracts` | Shared **request/response envelopes** and routing rules so both paths stay compatible at the edge. |
| `docs` | Architecture and ADRs (why split, when to merge results). |
| `infra` | CI/CD (GitHub Actions + OIDC), ECR, IAM. |

## Documentation

- [Architecture](docs/ARCHITECTURE.md) — data flow, failure domains, orchestration rules.
- [Architecture decisions](docs/DECISIONS.md) — ADR log.

## Current state

- **orchestration-edge**: Spring Boot 3.4 on **8082**, forwards to rag (`/v1/query`) or agent (`/v1/invoke`), `RestClient` timeouts, 502 on downstream failure, tests (`mvn verify`).
- **rag-gateway**: Spring Boot 3.4, `POST /v1/query`, stub RAG, Dockerfile.
- **agent-runtime**: FastAPI stub on **8081**, Dockerfile, pytest.
- **contracts**: JSON Schema + OpenAPI (including `orchestration-openapi.yaml`).
- **docker-compose.yml**: builds and runs all three services (optional local stack).
- **CI**: GitHub Actions runs Maven for both Java services and pytest for the agent.

## References

- [Deploy AI agents on Amazon Bedrock AgentCore using GitHub Actions](https://aws.amazon.com/blogs/machine-learning/deploy-ai-agents-on-amazon-bedrock-agentcore-using-github-actions/) (ECR, OIDC, AgentCore Runtime).
- AWS samples: [sample-bedrock-agentcore-runtime-cicd](https://github.com/aws-samples/sample-bedrock-agentcore-runtime-cicd) (reference pipeline layout).
