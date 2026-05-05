package dev.lordkay.orchestration.web;

import dev.lordkay.orchestration.downstream.DownstreamQueryException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
                "message", ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
  }
}
