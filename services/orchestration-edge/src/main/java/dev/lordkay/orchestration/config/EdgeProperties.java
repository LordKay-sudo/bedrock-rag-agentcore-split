package dev.lordkay.orchestration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "edge")
public class EdgeProperties {

  /** Base URL for rag-gateway (no trailing slash). */
  private String ragBaseUrl = "http://127.0.0.1:8080";

  /** Base URL for agent-runtime (no trailing slash). */
  private String agentBaseUrl = "http://127.0.0.1:8081";
  private final RateLimit rateLimit = new RateLimit();
  private final RequestGuard requestGuard = new RequestGuard();
  private final Budget budget = new Budget();
  private final Cache cache = new Cache();
  private final Resilience resilience = new Resilience();

  public String getRagBaseUrl() {
    return ragBaseUrl;
  }

  public void setRagBaseUrl(String ragBaseUrl) {
    this.ragBaseUrl = ragBaseUrl;
  }

  public String getAgentBaseUrl() {
    return agentBaseUrl;
  }

  public void setAgentBaseUrl(String agentBaseUrl) {
    this.agentBaseUrl = agentBaseUrl;
  }

  public RateLimit getRateLimit() {
    return rateLimit;
  }

  public RequestGuard getRequestGuard() {
    return requestGuard;
  }

  public Budget getBudget() {
    return budget;
  }

  public Cache getCache() {
    return cache;
  }

  public Resilience getResilience() {
    return resilience;
  }

  public static class RateLimit {
    private int capacity = 30;
    private int refillSeconds = 60;

    public int getCapacity() {
      return capacity;
    }

    public void setCapacity(int capacity) {
      this.capacity = capacity;
    }

    public int getRefillSeconds() {
      return refillSeconds;
    }

    public void setRefillSeconds(int refillSeconds) {
      this.refillSeconds = refillSeconds;
    }
  }

  public static class RequestGuard {
    private int maxMessageLength = 2000;

    public int getMaxMessageLength() {
      return maxMessageLength;
    }

    public void setMaxMessageLength(int maxMessageLength) {
      this.maxMessageLength = maxMessageLength;
    }
  }

  public static class Budget {
    private int maxRequestEstimatedTokens = 4000;
    private int maxDailyEstimatedTokensPerClient = 200000;

    public int getMaxRequestEstimatedTokens() {
      return maxRequestEstimatedTokens;
    }

    public void setMaxRequestEstimatedTokens(int maxRequestEstimatedTokens) {
      this.maxRequestEstimatedTokens = maxRequestEstimatedTokens;
    }

    public int getMaxDailyEstimatedTokensPerClient() {
      return maxDailyEstimatedTokensPerClient;
    }

    public void setMaxDailyEstimatedTokensPerClient(int maxDailyEstimatedTokensPerClient) {
      this.maxDailyEstimatedTokensPerClient = maxDailyEstimatedTokensPerClient;
    }
  }

  public static class Cache {
    private int ttlSeconds = 120;
    private boolean enabled = true;
    private long maximumSize = 5000;

    public int getTtlSeconds() {
      return ttlSeconds;
    }

    public void setTtlSeconds(int ttlSeconds) {
      this.ttlSeconds = ttlSeconds;
    }

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public long getMaximumSize() {
      return maximumSize;
    }

    public void setMaximumSize(long maximumSize) {
      this.maximumSize = maximumSize;
    }
  }

  public static class Resilience {
    private int retryAttempts = 2;
    private int circuitBreakerFailureThreshold = 3;
    private int circuitBreakerCooldownSeconds = 30;

    public int getRetryAttempts() {
      return retryAttempts;
    }

    public void setRetryAttempts(int retryAttempts) {
      this.retryAttempts = retryAttempts;
    }

    public int getCircuitBreakerFailureThreshold() {
      return circuitBreakerFailureThreshold;
    }

    public void setCircuitBreakerFailureThreshold(int circuitBreakerFailureThreshold) {
      this.circuitBreakerFailureThreshold = circuitBreakerFailureThreshold;
    }

    public int getCircuitBreakerCooldownSeconds() {
      return circuitBreakerCooldownSeconds;
    }

    public void setCircuitBreakerCooldownSeconds(int circuitBreakerCooldownSeconds) {
      this.circuitBreakerCooldownSeconds = circuitBreakerCooldownSeconds;
    }
  }
}
