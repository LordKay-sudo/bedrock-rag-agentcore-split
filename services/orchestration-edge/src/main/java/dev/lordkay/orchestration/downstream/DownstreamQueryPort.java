package dev.lordkay.orchestration.downstream;

import dev.lordkay.orchestration.model.QueryEnvelope;
import dev.lordkay.orchestration.model.QueryResponse;

public interface DownstreamQueryPort {

  QueryResponse forwardRag(QueryEnvelope envelope);

  QueryResponse forwardAgent(QueryEnvelope envelope);
}
