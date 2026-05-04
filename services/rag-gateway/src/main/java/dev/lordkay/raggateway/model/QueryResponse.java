package dev.lordkay.raggateway.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record QueryResponse(
    UUID correlationId, String pathUsed, String text, List<Citation> citations) {}
