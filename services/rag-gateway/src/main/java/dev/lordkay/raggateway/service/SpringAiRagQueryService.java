package dev.lordkay.raggateway.service;

import dev.lordkay.raggateway.config.RagAiProperties;
import dev.lordkay.raggateway.model.Citation;
import dev.lordkay.raggateway.model.PreferredPath;
import dev.lordkay.raggateway.model.QueryEnvelope;
import dev.lordkay.raggateway.model.QueryResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Foundation integration point for Spring AI. This intentionally stays lightweight: it proves
 * wiring, toggling, and dependency health before introducing retrieval/vector/database flow.
 */
@Service
@ConditionalOnClass(ChatClient.class)
@ConditionalOnProperty(prefix = "rag.ai", name = "enabled", havingValue = "true")
public class SpringAiRagQueryService implements RagQueryService {

  private final ChatClient chatClient;
  private final RagAiProperties properties;

  public SpringAiRagQueryService(ChatClient.Builder chatClientBuilder, RagAiProperties properties) {
    this.chatClient = chatClientBuilder.build();
    this.properties = properties;
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

    String text =
        chatClient
            .prompt()
            .system(properties.getSystemPrompt())
            .user(envelope.message())
            .call()
            .content();

    return new QueryResponse(
        correlationId,
        "rag",
        text != null ? text : "[spring-ai foundation] Empty model response.",
        List.of(
            new Citation(
                "ai-foundation",
                "Spring AI foundation active. Retrieval integration comes in the next PR.")));
  }
}
