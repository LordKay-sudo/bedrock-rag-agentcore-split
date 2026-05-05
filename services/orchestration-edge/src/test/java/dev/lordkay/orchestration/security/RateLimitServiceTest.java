package dev.lordkay.orchestration.security;

import static org.assertj.core.api.Assertions.assertThat;

import dev.lordkay.orchestration.config.EdgeProperties;
import org.junit.jupiter.api.Test;

class RateLimitServiceTest {

  @Test
  void deniesRequestsAfterCapacityReachedInWindow() {
    EdgeProperties properties = new EdgeProperties();
    properties.getRateLimit().setCapacity(2);
    properties.getRateLimit().setRefillSeconds(60);

    RateLimitService service = new RateLimitService(properties);

    assertThat(service.allow("ip:127.0.0.1")).isTrue();
    assertThat(service.allow("ip:127.0.0.1")).isTrue();
    assertThat(service.allow("ip:127.0.0.1")).isFalse();
  }
}
