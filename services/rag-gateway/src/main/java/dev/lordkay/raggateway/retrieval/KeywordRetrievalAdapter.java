package dev.lordkay.raggateway.retrieval;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Deterministic local retrieval adapter used until external vector retrieval is integrated. Keeps
 * tests and dev runs predictable while preserving retrieval flow shape.
 */
@Component
public class KeywordRetrievalAdapter implements RetrievalPort {

  @Override
  public List<RetrievedChunk> retrieve(String query, RetrievalOptions options) {
    String q = query.toLowerCase(Locale.ROOT);
    List<RetrievedChunk> all = new ArrayList<>();

    if (q.contains("refund") || q.contains("return")) {
      all.add(
          new RetrievedChunk(
              "policy-refund-v1",
              "Refunds are allowed within 30 days with proof of purchase. Processing can take 5-10 business days.",
              0.93,
              Map.of("category", "policy")));
    }

    if (q.contains("security") || q.contains("password") || q.contains("mfa")) {
      all.add(
          new RetrievedChunk(
              "security-controls-v2",
              "MFA is mandatory for privileged access. Password rotation is required every 90 days.",
              0.89,
              Map.of("category", "security")));
    }

    if (q.contains("invoice") || q.contains("payment") || q.contains("billing")) {
      all.add(
          new RetrievedChunk(
              "billing-ops-v3",
              "Invoice disputes must include invoice number, amount, and issue reason. SLA response time is 2 business days.",
              0.86,
              Map.of("category", "billing")));
    }

    return all.stream()
        .filter(c -> options.minScore() == null || (c.score() != null && c.score() >= options.minScore()))
        .limit(Math.max(1, options.topK()))
        .map(
            c ->
                new RetrievedChunk(
                    c.sourceId(),
                    truncate(c.excerpt(), options.maxExcerptChars()),
                    c.score(),
                    c.metadata()))
        .toList();
  }

  private static String truncate(String text, int maxChars) {
    int safeMax = Math.max(40, maxChars);
    return text.length() <= safeMax ? text : text.substring(0, safeMax);
  }
}
