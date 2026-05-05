package dev.lordkay.orchestration.web;

import dev.lordkay.orchestration.downstream.DownstreamQueryException;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class DownstreamExceptionHandler {

  @ExceptionHandler(DownstreamQueryException.class)
  public ResponseEntity<Map<String, Object>> onDownstream(DownstreamQueryException ex) {
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
        .body(
            Map.of(
                "error", "downstream_failed",
                "downstream", ex.getDownstream(),
                "downstreamStatus", ex.getStatus(),
                "message", "Service temporarily unavailable. Please retry.",
                "timestamp", Instant.now().toString()));
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String, Object>> onResponseStatus(ResponseStatusException ex) {
    return ResponseEntity.status(ex.getStatusCode())
        .body(
            Map.of(
                "error", "request_rejected",
                "message", ex.getReason() != null ? ex.getReason() : "invalid_request",
                "timestamp", Instant.now().toString()));
  }
}
