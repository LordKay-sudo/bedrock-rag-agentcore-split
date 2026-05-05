package dev.lordkay.orchestration.downstream;

import dev.lordkay.orchestration.model.QueryEnvelope;
import dev.lordkay.orchestration.model.QueryResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Service
public class RestDownstreamQueryClient implements DownstreamQueryPort {

  private final RestClient ragClient;
  private final RestClient agentClient;
  private final DownstreamResilienceService resilienceService;

  public RestDownstreamQueryClient(
      @Qualifier("ragDownstreamRestClient") RestClient ragClient,
      @Qualifier("agentDownstreamRestClient") RestClient agentClient,
      DownstreamResilienceService resilienceService) {
    this.ragClient = ragClient;
    this.agentClient = agentClient;
    this.resilienceService = resilienceService;
  }

  @Override
  public QueryResponse forwardRag(QueryEnvelope envelope) {
    return resilienceService.executeWithPolicy(
        "rag", () -> postJson(ragClient, "/v1/query", envelope, "rag"));
  }

  @Override
  public QueryResponse forwardAgent(QueryEnvelope envelope) {
    return resilienceService.executeWithPolicy(
        "agent", () -> postJson(agentClient, "/v1/invoke", envelope, "agent"));
  }

  private static QueryResponse postJson(
      RestClient client, String uri, QueryEnvelope body, String downstreamName) {
    try {
      return client
          .post()
          .uri(uri)
          .contentType(MediaType.APPLICATION_JSON)
          .body(body)
          .retrieve()
          .body(QueryResponse.class);
    } catch (RestClientResponseException ex) {
      throw new DownstreamQueryException(downstreamName, ex.getStatusCode().value(), ex);
    } catch (RestClientException ex) {
      throw new DownstreamQueryException(downstreamName, -1, ex);
    }
  }
}
