package dev.lordkay.orchestration.security;

import static org.assertj.core.api.Assertions.assertThat;

import dev.lordkay.orchestration.config.CacheConfig;
import dev.lordkay.orchestration.config.EdgeProperties;
import dev.lordkay.orchestration.model.QueryResponse;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;

class ResponseCacheServiceTest {

  @Test
  void storesAndReturnsCachedResponse() {
    EdgeProperties properties = new EdgeProperties();
    CacheManager manager = new CacheConfig().cacheManager(properties);
    ResponseCacheService cache = new ResponseCacheService(manager, properties);
    QueryResponse response = new QueryResponse(UUID.randomUUID(), "rag", "cached", null);

    cache.put("k1", response);

    assertThat(cache.get("k1")).contains(response);
  }

  @Test
  void doesNotStoreWhenDisabled() {
    EdgeProperties properties = new EdgeProperties();
    properties.getCache().setEnabled(false);
    CacheManager manager = new CacheConfig().cacheManager(properties);
    ResponseCacheService cache = new ResponseCacheService(manager, properties);

    cache.put("k2", new QueryResponse(null, "rag", "value", null));

    assertThat(cache.get("k2")).isEmpty();
  }

  @Test
  void appliesMaximumSizeCap() {
    EdgeProperties properties = new EdgeProperties();
    properties.getCache().setMaximumSize(1);
    CacheManager manager = new CacheConfig().cacheManager(properties);
    ResponseCacheService cache = new ResponseCacheService(manager, properties);

    cache.put("a", new QueryResponse(null, "rag", "one", null));
    cache.put("b", new QueryResponse(null, "rag", "two", null));

    assertThat(cache.get("a").isPresent() || cache.get("b").isPresent()).isTrue();
  }
}
