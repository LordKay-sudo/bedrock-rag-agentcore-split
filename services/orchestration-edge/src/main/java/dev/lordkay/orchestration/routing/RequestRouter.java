package dev.lordkay.orchestration.routing;

import dev.lordkay.orchestration.model.PreferredPath;
import dev.lordkay.orchestration.model.QueryEnvelope;
import java.util.Locale;
import org.springframework.stereotype.Component;

/**
 * Decides which downstream path handles the request. {@link PreferredPath#auto} uses a small
 * keyword heuristic until a classifier or policy engine replaces it.
 */
@Component
public class RequestRouter {

  public RoutingTarget route(QueryEnvelope envelope) {
    return switch (envelope.preferredPath()) {
      case rag -> RoutingTarget.RAG;
      case agent -> RoutingTarget.AGENT;
      case auto -> routeAuto(envelope.message());
    };
  }

  private RoutingTarget routeAuto(String message) {
    String m = message.toLowerCase(Locale.ROOT);
    if (m.contains("calculate")
        || m.contains("run tool")
        || m.contains("execute workflow")
        || m.contains("invoke agent")) {
      return RoutingTarget.AGENT;
    }
    return RoutingTarget.RAG;
  }
}
