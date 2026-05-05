package dev.lordkay.orchestration.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record QueryEnvelope(
    @NotBlank String message, PreferredPath preferredPath, UUID correlationId) {

  public QueryEnvelope {
    if (preferredPath == null) {
      preferredPath = PreferredPath.auto;
    }
  }
}
