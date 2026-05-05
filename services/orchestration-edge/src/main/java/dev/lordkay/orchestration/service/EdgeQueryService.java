package dev.lordkay.orchestration.service;

import dev.lordkay.orchestration.downstream.DownstreamQueryPort;
import dev.lordkay.orchestration.model.QueryEnvelope;
import dev.lordkay.orchestration.model.QueryResponse;
import dev.lordkay.orchestration.routing.RequestRouter;
import dev.lordkay.orchestration.routing.RoutingTarget;
import dev.lordkay.orchestration.security.RequestSanitizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EdgeQueryService {
  private static final Logger logger = LoggerFactory.getLogger(EdgeQueryService.class);

  private final RequestRouter requestRouter;
  private final DownstreamQueryPort downstream;
  private final RequestSanitizer requestSanitizer;

  public EdgeQueryService(
      RequestRouter requestRouter, DownstreamQueryPort downstream, RequestSanitizer requestSanitizer) {
    this.requestRouter = requestRouter;
    this.downstream = downstream;
    this.requestSanitizer = requestSanitizer;
  }

  public QueryResponse handle(QueryEnvelope envelope) {
    requestSanitizer.validate(envelope.message());
    RoutingTarget target = requestRouter.route(envelope);
    logger.info(
        "query_routed target={} preferredPath={} correlationId={}",
        target,
        envelope.preferredPath(),
        envelope.correlationId());
    return switch (target) {
      case RAG -> downstream.forwardRag(envelope);
      case AGENT -> downstream.forwardAgent(envelope);
    };
  }
}
