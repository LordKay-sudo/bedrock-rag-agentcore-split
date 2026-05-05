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
}
