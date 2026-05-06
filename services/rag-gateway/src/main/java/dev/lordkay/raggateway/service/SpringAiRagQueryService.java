package dev.lordkay.raggateway.service;

import dev.lordkay.raggateway.config.RagAiProperties;
import dev.lordkay.raggateway.model.Citation;
import dev.lordkay.raggateway.model.PreferredPath;
import dev.lordkay.raggateway.model.QueryEnvelope;
import dev.lordkay.raggateway.model.QueryResponse;
import dev.lordkay.raggateway.retrieval.RetrievedChunk;
import dev.lordkay.raggateway.retrieval.RetrievalOptions;
import dev.lordkay.raggateway.retrieval.RetrievalPort;
import java.util.List;
import java.util.UUID;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Foundation integration point for Spring AI. This intentionally stays lightweight: it proves
 * wiring, toggling, and dependency health before introducing retrieval/vector/database flow.
 */
@Service
@ConditionalOnClass(ChatClient.class)
@ConditionalOnProperty(prefix = "rag.ai", name = "enabled", havingValue = "true")
public class SpringAiRagQueryService implements RagQueryService {
  private static final Logger logger = LoggerFactory.getLogger(SpringAiRagQueryService.class);

  private final ChatClient chatClient;
  private final RagAiProperties properties;
  private final RetrievalPort retrievalPort;

  public SpringAiRagQueryService(
      ChatClient.Builder chatClientBuilder, RagAiProperties properties, RetrievalPort retrievalPort) {
    this.chatClient = chatClientBuilder.build();
    this.properties = properties;
    this.retrievalPort = retrievalPort;
  }

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

    RetrievalOptions options =
        new RetrievalOptions(
            properties.getRetrieval().getTopK(),
            normalizeMinScore(properties.getRetrieval().getMinScore()),
            properties.getRetrieval().getMaxExcerptChars());
    List<RetrievedChunk> chunks =
        properties.getRetrieval().isEnabled()
            ? retrievalPort.retrieve(envelope.message(), options)
            : List.of();

    logger.info(
        "rag_retrieval correlationId={} hits={} topK={} minScore={}",
        correlationId,
        chunks.size(),
        options.topK(),
        options.minScore());

    if (chunks.isEmpty() && "return_no_evidence".equalsIgnoreCase(properties.getRetrieval().getNoHitMode())) {
      return new QueryResponse(
          correlationId,
          "rag",
          "No grounded evidence found for this question. Please rephrase or provide more specific context.",
          List.of());
    }

    String userPrompt = buildGroundedPrompt(envelope.message(), chunks);
    String text;
    try {
      text =
          chatClient
              .prompt()
              .system(properties.getSystemPrompt())
              .user(userPrompt)
              .call()
              .content();
    } catch (RuntimeException ex) {
      logger.warn("rag_generation_failed correlationId={} reason={}", correlationId, ex.toString());
      return new QueryResponse(
          correlationId,
          "rag",
          "Generation temporarily unavailable. Please retry.",
          chunks.stream().map(c -> new Citation(c.sourceId(), c.excerpt())).toList());
    }

    return new QueryResponse(
        correlationId,
        "rag",
        text != null ? text : "No response generated.",
        chunks.stream().map(c -> new Citation(c.sourceId(), c.excerpt())).toList());
  }

  private static String buildGroundedPrompt(String userQuestion, List<RetrievedChunk> chunks) {
    if (chunks.isEmpty()) {
      return userQuestion;
    }
    StringBuilder b = new StringBuilder();
    b.append("User question:\n").append(userQuestion).append("\n\n");
    b.append("Use the following evidence when answering. If uncertain, say so.\n");
    for (int i = 0; i < chunks.size(); i++) {
      var c = chunks.get(i);
      b.append("- [")
          .append(i + 1)
          .append("] source=")
          .append(c.sourceId())
          .append(" excerpt=")
          .append(c.excerpt())
          .append('\n');
    }
    return b.toString();
  }

  private static Double normalizeMinScore(Double minScore) {
    if (minScore == null || minScore <= 0.0) {
      return null;
    }
    return minScore;
  }
}
