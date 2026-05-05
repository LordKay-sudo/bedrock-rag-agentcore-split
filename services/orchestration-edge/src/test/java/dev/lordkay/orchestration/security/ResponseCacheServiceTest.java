package dev.lordkay.orchestration.security;

import static org.assertj.core.api.Assertions.assertThat;

import dev.lordkay.orchestration.config.EdgeProperties;
import dev.lordkay.orchestration.model.QueryResponse;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ResponseCacheServiceTest {

  @Test
  void storesAndReturnsCachedResponse() {
    ResponseCacheService cache = new ResponseCacheService(new EdgeProperties());
    QueryResponse response = new QueryResponse(UUID.randomUUID(), "rag", "cached", null);

    cache.put("k1", response);

    assertThat(cache.get("k1")).contains(response);
  }
}
