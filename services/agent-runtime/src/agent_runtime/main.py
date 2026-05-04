"""Agent path HTTP stub.

Replace /v1/invoke with BedrockAgentCoreApp + tools when deploying to AgentCore Runtime.
"""

from __future__ import annotations

import uuid
from typing import Literal

from fastapi import FastAPI
from pydantic import BaseModel, Field

PreferredPath = Literal["rag", "agent", "auto"]
PathUsed = Literal["rag", "agent"]


class Citation(BaseModel):
    sourceId: str
    excerpt: str


class QueryEnvelope(BaseModel):
    message: str = Field(..., min_length=1)
    preferredPath: PreferredPath = "auto"
    correlationId: uuid.UUID | None = None

    def correlation_id_or_new(self) -> uuid.UUID:
        return self.correlationId or uuid.uuid4()


class QueryResponse(BaseModel):
    correlationId: uuid.UUID | None = None
    pathUsed: PathUsed
    text: str
    citations: list[Citation] | None = None


app = FastAPI(title="agent-runtime", version="0.1.0")


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "UP"}


@app.post("/v1/invoke", response_model=QueryResponse)
def invoke(body: QueryEnvelope) -> QueryResponse:
    cid = body.correlation_id_or_new()
    if body.preferredPath == "rag":
        return QueryResponse(
            correlationId=cid,
            pathUsed="rag",
            text=(
                "Agent runtime received preferredPath=rag; call the RAG gateway for "
                "retrieve-then-generate instead of this container."
            ),
            citations=None,
        )
    return QueryResponse(
        correlationId=cid,
        pathUsed="agent",
        text="[stub agent] No tools or Bedrock session yet. Echo: " + body.message[:200],
        citations=None,
    )


@app.post("/v1/query")
def query_alias(body: QueryEnvelope) -> QueryResponse:
    """Same contract as rag-gateway /v1/query for local symmetry tests."""
    return invoke(body)
