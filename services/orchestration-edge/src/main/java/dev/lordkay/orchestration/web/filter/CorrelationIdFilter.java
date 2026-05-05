package dev.lordkay.orchestration.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {
  public static final String CORRELATION_HEADER = "X-Correlation-Id";
  public static final String CORRELATION_ATTR = "correlationId";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String incoming = request.getHeader(CORRELATION_HEADER);
    String correlationId = normalizeCorrelationId(incoming);
    request.setAttribute(CORRELATION_ATTR, correlationId);
    response.setHeader(CORRELATION_HEADER, correlationId);
    MDC.put(CORRELATION_ATTR, correlationId);
    try {
      filterChain.doFilter(request, response);
    } finally {
      MDC.remove(CORRELATION_ATTR);
    }
  }

  private static String normalizeCorrelationId(String incoming) {
    if (incoming == null || incoming.isBlank()) {
      return UUID.randomUUID().toString();
    }
    try {
      return UUID.fromString(incoming).toString();
    } catch (IllegalArgumentException ignored) {
      return UUID.randomUUID().toString();
    }
  }
}
