package dev.lordkay.raggateway.retrieval;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class KeywordRetrievalAdapterTest {

  private final KeywordRetrievalAdapter adapter = new KeywordRetrievalAdapter();

  @Test
  void returnsDeterministicPolicyChunkForRefundQuestion() {
    var chunks = adapter.retrieve("What is your refund policy?", new RetrievalOptions(5, null, 300));

    assertThat(chunks).isNotEmpty();
    assertThat(chunks.get(0).sourceId()).isEqualTo("policy-refund-v1");
  }

  @Test
  void respectsTopKLimit() {
    var chunks =
        adapter.retrieve(
            "refund security billing",
            new RetrievalOptions(2, null, 300));

    assertThat(chunks).hasSize(2);
  }

  @Test
  void returnsEmptyWhenNoKeywordsMatch() {
    var chunks = adapter.retrieve("completely unrelated topic", new RetrievalOptions(5, null, 300));

    assertThat(chunks).isEmpty();
  }
}
