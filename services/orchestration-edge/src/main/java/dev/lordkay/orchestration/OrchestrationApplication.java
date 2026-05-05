package dev.lordkay.orchestration;

import dev.lordkay.orchestration.config.EdgeProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableConfigurationProperties(EdgeProperties.class)
@EnableCaching
public class OrchestrationApplication {

  public static void main(String[] args) {
    SpringApplication.run(OrchestrationApplication.class, args);
  }
}
