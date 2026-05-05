# Orchestration edge

Spring Boot service that exposes **`POST /v1/query`** and forwards the shared envelope to **rag-gateway** (`/v1/query`) or **agent-runtime** (`/v1/invoke`) based on `preferredPath` and a small **auto** routing heuristic.

## Run

JDK 17 + Maven. Start **rag-gateway** (8080) and **agent-runtime** (8081) first, then:

```bash
mvn spring-boot:run
```

Edge listens on **8082**.

### Downstream URLs

Configure in `application.yml` or environment (Spring relaxed binding):

| Property | Default |
|----------|---------|
| `edge.rag-base-url` | `http://127.0.0.1:8080` |
| `edge.agent-base-url` | `http://127.0.0.1:8081` |

Docker Compose sets `EDGE_RAG_BASE_URL` and `EDGE_AGENT_BASE_URL` for service DNS names.

## Tests

```bash
mvn verify
```
