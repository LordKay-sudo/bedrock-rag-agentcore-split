# Agent runtime

FastAPI stub for the **agent / tools** path. Same JSON envelope as the RAG gateway for local testing. For production, replace the HTTP app with `BedrockAgentCoreApp` and a container entrypoint per AWS AgentCore Runtime docs.

## Run

Python 3.10+.

```bash
pip install -r requirements.txt
set PYTHONPATH=src
uvicorn agent_runtime.main:app --host 0.0.0.0 --port 8081
```

(PowerShell: `$env:PYTHONPATH='src'`.)

## Endpoints

- `POST /v1/invoke` — primary agent surface (stub).
- `POST /v1/query` — alias for parity with `rag-gateway`.
- `GET /health` — liveness for containers.

## Tests

```bash
pip install -r requirements-dev.txt
pytest
```

## Container

```bash
docker build -t agent-runtime:local .
docker run --rm -p 8081:8081 agent-runtime:local
```
