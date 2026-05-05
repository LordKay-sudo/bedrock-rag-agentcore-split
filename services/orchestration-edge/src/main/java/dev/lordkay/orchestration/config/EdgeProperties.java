package dev.lordkay.orchestration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "edge")
public class EdgeProperties {

  /** Base URL for rag-gateway (no trailing slash). */
  private String ragBaseUrl = "http://127.0.0.1:8080";

  /** Base URL for agent-runtime (no trailing slash). */
  private String agentBaseUrl = "http://127.0.0.1:8081";

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
}
