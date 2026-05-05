package dev.lordkay.orchestration.service;

import dev.lordkay.orchestration.downstream.DownstreamQueryPort;
import dev.lordkay.orchestration.model.QueryEnvelope;
import dev.lordkay.orchestration.model.QueryResponse;
import dev.lordkay.orchestration.routing.RequestRouter;
import dev.lordkay.orchestration.routing.RoutingTarget;
import org.springframework.stereotype.Service;

@Service
public class EdgeQueryService {

  private final RequestRouter requestRouter;
  private final DownstreamQueryPort downstream;

  public EdgeQueryService(RequestRouter requestRouter, DownstreamQueryPort downstream) {
    this.requestRouter = requestRouter;
    this.downstream = downstream;
  }

  public QueryResponse handle(QueryEnvelope envelope) {
    RoutingTarget target = requestRouter.route(envelope);
    return switch (target) {
      case RAG -> downstream.forwardRag(envelope);
      case AGENT -> downstream.forwardAgent(envelope);
    };
  }
}
