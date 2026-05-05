package dev.lordkay.orchestration.security;

import dev.lordkay.orchestration.config.EdgeProperties;
import dev.lordkay.orchestration.model.QueryResponse;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ResponseCacheService {
  private record CacheEntry(QueryResponse value, Instant expiresAt) {}

  private final Map<String, CacheEntry> store = new ConcurrentHashMap<>();
  private final EdgeProperties properties;

  public ResponseCacheService(EdgeProperties properties) {
    this.properties = properties;
  }

  public Optional<QueryResponse> get(String key) {
    CacheEntry entry = store.get(key);
    if (entry == null) {
      return Optional.empty();
    }
    if (entry.expiresAt.isBefore(Instant.now())) {
      store.remove(key);
      return Optional.empty();
    }
    return Optional.of(entry.value);
  }

  public void put(String key, QueryResponse value) {
    if (!properties.getCache().isEnabled()) {
      return;
    }
    Instant expires = Instant.now().plusSeconds(properties.getCache().getTtlSeconds());
    store.put(key, new CacheEntry(value, expires));
  }
}
