package dev.lordkay.orchestration.web.interceptor;

import dev.lordkay.orchestration.security.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
  private final RateLimitService rateLimitService;

  public RateLimitInterceptor(RateLimitService rateLimitService) {
    this.rateLimitService = rateLimitService;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String key = resolveKey(request);
    if (!rateLimitService.allow(key)) {
      throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "rate_limit_exceeded");
    }
    return true;
  }

  private static String resolveKey(HttpServletRequest request) {
    String apiKey = request.getHeader("X-API-Key");
    if (apiKey != null && !apiKey.isBlank()) {
      return "api:" + apiKey;
    }
    String forwardedFor = request.getHeader("X-Forwarded-For");
    if (forwardedFor != null && !forwardedFor.isBlank()) {
      return "ip:" + forwardedFor.split(",")[0].trim();
    }
    return "ip:" + request.getRemoteAddr();
  }
}
