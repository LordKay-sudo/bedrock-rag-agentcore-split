package dev.lordkay.raggateway;

import dev.lordkay.raggateway.config.RagAiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RagAiProperties.class)
public class RagGatewayApplication {

  public static void main(String[] args) {
    SpringApplication.run(RagGatewayApplication.class, args);
  }
}
