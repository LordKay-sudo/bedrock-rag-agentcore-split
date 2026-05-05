package dev.lordkay.orchestration.security;

import dev.lordkay.orchestration.config.EdgeProperties;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class BudgetGuardService {
  private record DailyUsage(LocalDate day, int tokens) {}

  private final Map<String, DailyUsage> usage = new ConcurrentHashMap<>();
  private final EdgeProperties properties;

  public BudgetGuardService(EdgeProperties properties) {
    this.properties = properties;
  }

  public void validateAndConsume(String clientKey, String message) {
    int estimatedTokens = estimateTokens(message);
    if (estimatedTokens > properties.getBudget().getMaxRequestEstimatedTokens()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "request_token_cap_exceeded");
    }

    LocalDate today = LocalDate.now();
    usage.compute(
        clientKey,
        (k, current) -> {
          DailyUsage base =
              (current == null || !current.day.equals(today)) ? new DailyUsage(today, 0) : current;
          int next = base.tokens + estimatedTokens;
          if (next > properties.getBudget().getMaxDailyEstimatedTokensPerClient()) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "daily_budget_exceeded");
          }
          return new DailyUsage(today, next);
        });
  }

  private static int estimateTokens(String message) {
    return Math.max(1, message.length() / 4);
  }
}
