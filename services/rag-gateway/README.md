# RAG gateway

Spring Boot service for the **retrieve-then-generate** path. `StubRagQueryService` returns deterministic placeholder content until Knowledge Base + Bedrock are wired.

## Spring AI foundation mode

The service now includes a **feature-flagged** Spring AI foundation path.

- Default behavior remains stub-safe (`RAG_AI_ENABLED=false`).
- When enabled (`RAG_AI_ENABLED=true`), `SpringAiRagQueryService` is activated and calls a Bedrock Converse chat model via Spring AI.
- Retrieval/KB grounding is intentionally deferred to the next PR to keep this step low-risk.

Example env vars:

```bash
RAG_AI_ENABLED=true
AWS_REGION=us-east-1
RAG_AI_MODEL=us.amazon.nova-pro-v1:0
RAG_AI_TEMPERATURE=0.2
RAG_AI_MAX_TOKENS=1024
```

## Retrieval mode (current PR)

When `RAG_AI_ENABLED=true`, the AI path now executes a retrieve-then-generate flow through a retrieval abstraction.

- `RetrievalPort` defines retrieval contract.
- `KeywordRetrievalAdapter` is the deterministic local adapter used for predictable tests/dev.
- Retrieved chunks are mapped to `QueryResponse.citations` (`sourceId`, `excerpt`).

Retrieval settings:

```bash
RAG_RETRIEVAL_ENABLED=true
RAG_RETRIEVAL_TOP_K=5
RAG_RETRIEVAL_MIN_SCORE=0.0
RAG_RETRIEVAL_MAX_EXCERPT_CHARS=300
RAG_RETRIEVAL_NO_HIT_MODE=answer_without_citations
```

`RAG_RETRIEVAL_NO_HIT_MODE` values:
- `answer_without_citations` -> continue generation without retrieved evidence.
- `return_no_evidence` -> return deterministic no-evidence response with empty citations.

## Observability and guardrails

The AI path emits lightweight metrics via Micrometer:

- `rag.retrieval.requests`
- `rag.retrieval.hits`
- `rag.generation.duration`
- `rag.generation.success`
- `rag.generation.failures`

Safety behavior:

- no-hit mode can return deterministic no-evidence responses
- generation failures return sanitized user-safe fallback text
- logs include retrieval hit counts and correlation identifiers, but avoid logging full prompt bodies

## Run

Requires JDK 17 and Maven 3.9+.

```bash
mvn spring-boot:run
```

## API

- `POST /v1/query` — body matches `contracts/query-envelope.schema.json`.
- `GET /actuator/health` — liveness.

Example:

```bash
curl -s localhost:8080/v1/query -H "Content-Type: application/json" \
  -d '{"message":"What is the refund policy?"}' | jq .
```

## Tests

```bash
mvn verify
```
