package dev.lordkay.raggateway.service;

import dev.lordkay.raggateway.model.Citation;
import dev.lordkay.raggateway.model.PreferredPath;
import dev.lordkay.raggateway.model.QueryEnvelope;
import dev.lordkay.raggateway.model.QueryResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Placeholder RAG implementation. Replace with Knowledge Base retrieval + Bedrock Converse
 * (or Spring AI) while keeping the same {@link QueryEnvelope} / {@link QueryResponse} types.
 */
@Service
@Primary
public class StubRagQueryService implements RagQueryService {

  @Override
  public QueryResponse answer(QueryEnvelope envelope) {
    UUID correlationId =
        envelope.correlationId() != null ? envelope.correlationId() : UUID.randomUUID();

    if (envelope.preferredPath() == PreferredPath.agent) {
      return new QueryResponse(
          correlationId,
          "agent",
          "RAG gateway received preferredPath=agent; route to AgentCore from the orchestration edge instead of calling this service.",
          null);
    }

    String stubAnswer =
        "[stub RAG] No retrieval run yet. Your message was: "
            + envelope.message().substring(0, Math.min(200, envelope.message().length()));

    return new QueryResponse(
        correlationId,
        "rag",
        stubAnswer,
        List.of(
            new Citation(
                "stub-doc-1",
                "Replace StubRagQueryService with KB-backed retrieve-then-generate.")));
  }
}
