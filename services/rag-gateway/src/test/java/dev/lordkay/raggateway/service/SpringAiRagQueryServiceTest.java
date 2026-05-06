package dev.lordkay.raggateway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.lordkay.raggateway.config.RagAiProperties;
import dev.lordkay.raggateway.model.PreferredPath;
import dev.lordkay.raggateway.model.QueryEnvelope;
import dev.lordkay.raggateway.retrieval.RetrievalOptions;
import dev.lordkay.raggateway.retrieval.RetrievalPort;
import java.util.List;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.chat.client.ChatClient;

class SpringAiRagQueryServiceTest {

  @Test
  void returnsNoEvidenceResponseWhenConfiguredAndRetrievalEmpty() {
    ChatClient.Builder builder = Mockito.mock(ChatClient.Builder.class);
    ChatClient chatClient = Mockito.mock(ChatClient.class, Mockito.RETURNS_DEEP_STUBS);
    when(builder.build()).thenReturn(chatClient);

    RetrievalPort retrieval = Mockito.mock(RetrievalPort.class);
    when(retrieval.retrieve(anyString(), Mockito.any(RetrievalOptions.class))).thenReturn(List.of());

    RagAiProperties props = new RagAiProperties();
    props.getRetrieval().setNoHitMode("return_no_evidence");

    SpringAiRagQueryService service =
        new SpringAiRagQueryService(builder, props, retrieval, new SimpleMeterRegistry());

    var response = service.answer(new QueryEnvelope("unknown topic", PreferredPath.rag, null));

    assertThat(response.pathUsed()).isEqualTo("rag");
    assertThat(response.citations()).isEmpty();
    assertThat(response.text()).contains("No grounded evidence found");
    verify(chatClient, never()).prompt();
  }

  @Test
  void returnsDeterministicFailureMessageWhenGenerationThrows() {
    ChatClient.Builder builder = Mockito.mock(ChatClient.Builder.class);
    ChatClient chatClient = Mockito.mock(ChatClient.class, Mockito.RETURNS_DEEP_STUBS);
    when(builder.build()).thenReturn(chatClient);

    RetrievalPort retrieval = Mockito.mock(RetrievalPort.class);
    when(retrieval.retrieve(anyString(), Mockito.any(RetrievalOptions.class)))
        .thenReturn(List.of(new dev.lordkay.raggateway.retrieval.RetrievedChunk("s1", "e1", 0.9, java.util.Map.of())));
    when(chatClient.prompt().system(anyString()).user(anyString()).call().content())
        .thenThrow(new RuntimeException("provider unavailable"));

    RagAiProperties props = new RagAiProperties();
    SpringAiRagQueryService service =
        new SpringAiRagQueryService(builder, props, retrieval, new SimpleMeterRegistry());

    var response = service.answer(new QueryEnvelope("refund policy?", PreferredPath.rag, null));

    assertThat(response.pathUsed()).isEqualTo("rag");
    assertThat(response.text()).isEqualTo("Generation temporarily unavailable. Please retry.");
    assertThat(response.citations()).hasSize(1);
  }
}
