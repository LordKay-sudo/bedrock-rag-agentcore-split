package dev.lordkay.raggateway.retrieval;

public record RetrievalOptions(int topK, Double minScore, int maxExcerptChars) {}
