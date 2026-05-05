package dev.lordkay.orchestration.downstream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.lordkay.orchestration.config.EdgeProperties;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class DownstreamResilienceServiceTest {

  @Test
  void opensCircuitAfterConfiguredFailures() {
    EdgeProperties props = new EdgeProperties();
    props.getResilience().setRetryAttempts(1);
    props.getResilience().setCircuitBreakerFailureThreshold(2);
    props.getResilience().setCircuitBreakerCooldownSeconds(60);
    DownstreamResilienceService service = new DownstreamResilienceService(props);

    assertThatThrownBy(
            () ->
                service.executeWithPolicy(
                    "rag",
                    () -> {
                      throw new DownstreamQueryException("rag", 500, new RuntimeException("boom"));
                    }))
        .isInstanceOf(DownstreamQueryException.class);

    assertThatThrownBy(
            () ->
                service.executeWithPolicy(
                    "rag",
                    () -> {
                      throw new DownstreamQueryException("rag", 500, new RuntimeException("boom"));
                    }))
        .isInstanceOf(DownstreamQueryException.class);

    assertThatThrownBy(
            () ->
                service.executeWithPolicy(
                    "rag",
                    () -> {
                      throw new DownstreamQueryException("rag", 500, new RuntimeException("boom"));
                    }))
        .isInstanceOf(ResponseStatusException.class);
  }
}
