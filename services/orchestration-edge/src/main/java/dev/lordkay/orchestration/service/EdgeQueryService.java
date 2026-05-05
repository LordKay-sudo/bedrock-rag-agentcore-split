package dev.lordkay.orchestration.service;

import dev.lordkay.orchestration.downstream.DownstreamQueryPort;
import dev.lordkay.orchestration.model.QueryEnvelope;
import dev.lordkay.orchestration.model.QueryResponse;
import dev.lordkay.orchestration.routing.RequestRouter;
import dev.lordkay.orchestration.routing.RoutingTarget;
import dev.lordkay.orchestration.security.BudgetGuardService;
import dev.lordkay.orchestration.security.RequestSanitizer;
import dev.lordkay.orchestration.security.ResponseCacheService;
import dev.lordkay.orchestration.web.interceptor.RateLimitInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class EdgeQueryService {
  private static final Logger logger = LoggerFactory.getLogger(EdgeQueryService.class);

  private final RequestRouter requestRouter;
  private final DownstreamQueryPort downstream;
  private final RequestSanitizer requestSanitizer;
  private final BudgetGuardService budgetGuardService;
  private final ResponseCacheService responseCacheService;

  public EdgeQueryService(
      RequestRouter requestRouter,
      DownstreamQueryPort downstream,
      RequestSanitizer requestSanitizer,
      BudgetGuardService budgetGuardService,
      ResponseCacheService responseCacheService) {
    this.requestRouter = requestRouter;
    this.downstream = downstream;
    this.requestSanitizer = requestSanitizer;
    this.budgetGuardService = budgetGuardService;
    this.responseCacheService = responseCacheService;
  }

  public QueryResponse handle(QueryEnvelope envelope) {
    requestSanitizer.validate(envelope.message());
    String clientKey = resolveClientKey();
    budgetGuardService.validateAndConsume(clientKey, envelope.message());
    RoutingTarget target = requestRouter.route(envelope);
    String cacheKey = clientKey + "|" + target + "|" + envelope.message().trim().toLowerCase();
    if (target == RoutingTarget.RAG) {
      var cached = responseCacheService.get(cacheKey);
      if (cached.isPresent()) {
        logger.info("query_cache_hit target={} clientKey={} correlationId={}", target, clientKey, envelope.correlationId());
        return cached.get();
      }
    }
    logger.info(
        "query_routed target={} preferredPath={} clientKey={} correlationId={}",
        target,
        envelope.preferredPath(),
        clientKey,
        envelope.correlationId());
    QueryResponse response =
        switch (target) {
      case RAG -> downstream.forwardRag(envelope);
      case AGENT -> downstream.forwardAgent(envelope);
    };
    if (target == RoutingTarget.RAG) {
      responseCacheService.put(cacheKey, response);
    }
    return response;
  }

  private static String resolveClientKey() {
    ServletRequestAttributes attrs =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attrs == null) {
      return "unknown";
    }
    Object fromRequest = attrs.getRequest().getAttribute(RateLimitInterceptor.CLIENT_KEY_ATTR);
    return fromRequest != null ? fromRequest.toString() : "unknown";
  }
}
