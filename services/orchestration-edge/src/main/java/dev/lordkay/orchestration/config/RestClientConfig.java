package dev.lordkay.orchestration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

  private static SimpleClientHttpRequestFactory requestFactory() {
    SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
    f.setConnectTimeout(2000);
    f.setReadTimeout(30000);
    return f;
  }

  @Bean
  public RestClient ragDownstreamRestClient(EdgeProperties props) {
    return RestClient.builder()
        .baseUrl(props.getRagBaseUrl())
        .requestFactory(requestFactory())
        .build();
  }

  @Bean
  public RestClient agentDownstreamRestClient(EdgeProperties props) {
    return RestClient.builder()
        .baseUrl(props.getAgentBaseUrl())
        .requestFactory(requestFactory())
        .build();
  }
}
