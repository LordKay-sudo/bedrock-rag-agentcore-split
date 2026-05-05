package dev.lordkay.orchestration.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.lordkay.orchestration.config.EdgeProperties;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class BudgetGuardServiceTest {

  @Test
  void rejectsRequestWhenPerRequestEstimateExceedsCap() {
    EdgeProperties props = new EdgeProperties();
    props.getBudget().setMaxRequestEstimatedTokens(2);
    BudgetGuardService service = new BudgetGuardService(props);

    assertThrows(
        ResponseStatusException.class,
        () -> service.validateAndConsume("api:test", "this message is clearly longer than 8 chars"));
  }

  @Test
  void allowsRequestUnderBudget() {
    BudgetGuardService service = new BudgetGuardService(new EdgeProperties());

    assertDoesNotThrow(() -> service.validateAndConsume("api:test", "short message"));
  }
}
