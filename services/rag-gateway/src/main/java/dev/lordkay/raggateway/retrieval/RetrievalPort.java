package dev.lordkay.raggateway.retrieval;

import java.util.List;

public interface RetrievalPort {
  List<RetrievedChunk> retrieve(String query, RetrievalOptions options);
}
