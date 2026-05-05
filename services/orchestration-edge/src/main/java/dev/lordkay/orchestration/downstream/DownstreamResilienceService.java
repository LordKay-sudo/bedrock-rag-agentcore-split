package dev.lordkay.orchestration.downstream;

import dev.lordkay.orchestration.config.EdgeProperties;
import dev.lordkay.orchestration.model.QueryResponse;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class DownstreamResilienceService {
  private record CircuitState(int failures, Instant openUntil) {}

  private final Map<String, CircuitState> states = new ConcurrentHashMap<>();
  private final EdgeProperties properties;

  public DownstreamResilienceService(EdgeProperties properties) {
    this.properties = properties;
  }

  public QueryResponse executeWithPolicy(String downstream, Supplier<QueryResponse> call) {
    ensureCircuitClosed(downstream);
    int attempts = Math.max(1, properties.getResilience().getRetryAttempts());
    RuntimeException last = null;
    for (int i = 0; i < attempts; i++) {
      try {
        QueryResponse response = call.get();
        states.put(downstream, new CircuitState(0, Instant.EPOCH));
        return response;
      } catch (DownstreamQueryException ex) {
        last = ex;
        registerFailure(downstream);
      }
    }
    throw last != null ? last : new ResponseStatusException(HttpStatus.BAD_GATEWAY, "downstream_failed");
  }

  private void ensureCircuitClosed(String downstream) {
    CircuitState state = states.get(downstream);
    if (state != null && state.openUntil.isAfter(Instant.now())) {
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "downstream_circuit_open");
    }
  }

  private void registerFailure(String downstream) {
    int threshold = properties.getResilience().getCircuitBreakerFailureThreshold();
    int cooldownSeconds = properties.getResilience().getCircuitBreakerCooldownSeconds();
    states.compute(
        downstream,
        (k, current) -> {
          CircuitState base = current == null ? new CircuitState(0, Instant.EPOCH) : current;
          int nextFailures = base.failures + 1;
          if (nextFailures >= threshold) {
            return new CircuitState(0, Instant.now().plusSeconds(cooldownSeconds));
          }
          return new CircuitState(nextFailures, Instant.EPOCH);
        });
  }
}
