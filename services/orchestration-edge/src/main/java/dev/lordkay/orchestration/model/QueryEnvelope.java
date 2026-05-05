package dev.lordkay.orchestration.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record QueryEnvelope(
    @NotBlank @Size(max = 2000) String message, PreferredPath preferredPath, UUID correlationId) {

  public QueryEnvelope {
    if (preferredPath == null) {
      preferredPath = PreferredPath.auto;
    }
    if (correlationId == null) {
      correlationId = UUID.randomUUID();
    }
  }
}
