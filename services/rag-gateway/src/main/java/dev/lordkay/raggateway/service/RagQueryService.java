package dev.lordkay.raggateway.service;

import dev.lordkay.raggateway.model.QueryEnvelope;
import dev.lordkay.raggateway.model.QueryResponse;

public interface RagQueryService {

  QueryResponse answer(QueryEnvelope envelope);
}
