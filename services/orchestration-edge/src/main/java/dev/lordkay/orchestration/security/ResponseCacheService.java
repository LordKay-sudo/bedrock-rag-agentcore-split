package dev.lordkay.orchestration.security;

import dev.lordkay.orchestration.config.CacheConfig;
import dev.lordkay.orchestration.config.EdgeProperties;
import dev.lordkay.orchestration.model.QueryResponse;
import java.util.Optional;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class ResponseCacheService {
  private final Cache cache;
  private final EdgeProperties properties;

  public ResponseCacheService(CacheManager cacheManager, EdgeProperties properties) {
    this.cache = cacheManager.getCache(CacheConfig.QUERY_RESPONSE_CACHE);
    this.properties = properties;
  }

  public Optional<QueryResponse> get(String key) {
    if (cache == null) {
      return Optional.empty();
    }
    QueryResponse value = cache.get(key, QueryResponse.class);
    return Optional.ofNullable(value);
  }

  public void put(String key, QueryResponse value) {
    if (!properties.getCache().isEnabled() || cache == null) {
      return;
    }
    cache.put(key, value);
  }
}
