package dev.lordkay.orchestration.web;

import dev.lordkay.orchestration.model.QueryEnvelope;
import dev.lordkay.orchestration.model.QueryResponse;
import dev.lordkay.orchestration.service.EdgeQueryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class EdgeQueryController {

  private final EdgeQueryService edgeQueryService;

  public EdgeQueryController(EdgeQueryService edgeQueryService) {
    this.edgeQueryService = edgeQueryService;
  }

  @PostMapping("/query")
  public QueryResponse query(@Valid @RequestBody QueryEnvelope envelope) {
    return edgeQueryService.handle(envelope);
  }
}
