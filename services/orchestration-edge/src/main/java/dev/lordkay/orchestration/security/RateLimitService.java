package dev.lordkay.orchestration.security;

import dev.lordkay.orchestration.config.EdgeProperties;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class RateLimitService {
  private record Window(long windowStartedEpochMs, int count) {}

  private final Map<String, Window> counters = new ConcurrentHashMap<>();
  private final EdgeProperties properties;

  public RateLimitService(EdgeProperties properties) {
    this.properties = properties;
  }

  public boolean allow(String key) {
    long now = System.currentTimeMillis();
    long windowMs = Duration.ofSeconds(properties.getRateLimit().getRefillSeconds()).toMillis();
    int capacity = properties.getRateLimit().getCapacity();

    Window current = counters.get(key);
    if (current == null || now - current.windowStartedEpochMs >= windowMs) {
      counters.put(key, new Window(now, 1));
      return true;
    }
    if (current.count >= capacity) {
      return false;
    }
    counters.put(key, new Window(current.windowStartedEpochMs, current.count + 1));
    return true;
  }
}
