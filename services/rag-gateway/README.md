# RAG gateway

Spring Boot service for the **retrieve-then-generate** path. `StubRagQueryService` returns deterministic placeholder content until Knowledge Base + Bedrock are wired.

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
