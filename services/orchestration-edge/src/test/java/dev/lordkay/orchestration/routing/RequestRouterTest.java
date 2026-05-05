package dev.lordkay.orchestration.routing;

import static org.assertj.core.api.Assertions.assertThat;

import dev.lordkay.orchestration.model.PreferredPath;
import dev.lordkay.orchestration.model.QueryEnvelope;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RequestRouterTest {

  private final RequestRouter router = new RequestRouter();

  @Test
  void explicitRag() {
    assertThat(router.route(new QueryEnvelope("x", PreferredPath.rag, null)))
        .isEqualTo(RoutingTarget.RAG);
  }

  @Test
  void explicitAgent() {
    assertThat(router.route(new QueryEnvelope("x", PreferredPath.agent, null)))
        .isEqualTo(RoutingTarget.AGENT);
  }

  @Test
  void autoDefaultsToRag() {
    assertThat(router.route(new QueryEnvelope("What is the policy?", PreferredPath.auto, null)))
        .isEqualTo(RoutingTarget.RAG);
  }

  @Test
  void autoKeywordRoutesToAgent() {
    assertThat(router.route(new QueryEnvelope("Please calculate the total", PreferredPath.auto, null)))
        .isEqualTo(RoutingTarget.AGENT);
  }

  @Test
  void correlationIdPreservedInEnvelope() {
    UUID id = UUID.randomUUID();
    var env = new QueryEnvelope("invoke agent for me", PreferredPath.auto, id);
    assertThat(env.correlationId()).isEqualTo(id);
    assertThat(router.route(env)).isEqualTo(RoutingTarget.AGENT);
  }
}
