package dev.lordkay.orchestration.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {
  public static final String QUERY_RESPONSE_CACHE = "queryResponseCache";

  @Bean
  public CacheManager cacheManager(EdgeProperties properties) {
    CaffeineCacheManager manager = new CaffeineCacheManager(QUERY_RESPONSE_CACHE);
    manager.setAllowNullValues(false);
    manager.setCaffeine(
        Caffeine.newBuilder()
            .maximumSize(properties.getCache().getMaximumSize())
            .expireAfterWrite(properties.getCache().getTtlSeconds(), TimeUnit.SECONDS));
    return manager;
  }
}
