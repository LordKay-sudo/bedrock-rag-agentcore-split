package dev.lordkay.raggateway.retrieval;

import java.util.Map;

public record RetrievedChunk(
    String sourceId, String excerpt, Double score, Map<String, String> metadata) {}
