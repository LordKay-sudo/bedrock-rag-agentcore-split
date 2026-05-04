package dev.lordkay.raggateway.web;

import dev.lordkay.raggateway.model.QueryEnvelope;
import dev.lordkay.raggateway.model.QueryResponse;
import dev.lordkay.raggateway.service.RagQueryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class QueryController {

  private final RagQueryService ragQueryService;

  public QueryController(RagQueryService ragQueryService) {
    this.ragQueryService = ragQueryService;
  }

  @PostMapping("/query")
  public QueryResponse query(@Valid @RequestBody QueryEnvelope envelope) {
    return ragQueryService.answer(envelope);
  }
}
