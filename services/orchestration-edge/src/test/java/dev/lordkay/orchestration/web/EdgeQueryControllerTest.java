package dev.lordkay.orchestration.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.lordkay.orchestration.downstream.DownstreamQueryPort;
import dev.lordkay.orchestration.model.Citation;
import dev.lordkay.orchestration.model.QueryResponse;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class EdgeQueryControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private DownstreamQueryPort downstream;

  @BeforeEach
  void resetMock() {
    org.mockito.Mockito.reset(downstream);
  }

  @Test
  void preferredRagCallsRagOnly() throws Exception {
    UUID cid = UUID.randomUUID();
    when(downstream.forwardRag(any()))
        .thenReturn(new QueryResponse(cid, "rag", "from-rag", List.of(new Citation("a", "b"))));

    mockMvc
        .perform(
            post("/v1/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"hello\",\"preferredPath\":\"rag\"}"))
        .andExpect(status().isOk())
        .andExpect(header().exists("X-Correlation-Id"))
        .andExpect(jsonPath("$.pathUsed").value("rag"))
        .andExpect(jsonPath("$.text").value("from-rag"));

    verify(downstream).forwardRag(any());
    verify(downstream, never()).forwardAgent(any());
  }

  @Test
  void preferredAgentCallsAgentOnly() throws Exception {
    when(downstream.forwardAgent(any()))
        .thenReturn(new QueryResponse(null, "agent", "from-agent", null));

    mockMvc
        .perform(
            post("/v1/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"hello\",\"preferredPath\":\"agent\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.pathUsed").value("agent"));

    verify(downstream).forwardAgent(any());
    verify(downstream, never()).forwardRag(any());
  }

  @Test
  void autoWithKeywordCallsAgent() throws Exception {
    when(downstream.forwardAgent(any()))
        .thenReturn(new QueryResponse(null, "agent", "tool", null));

    mockMvc
        .perform(
            post("/v1/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"calculate 2+2\",\"preferredPath\":\"auto\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.pathUsed").value("agent"));

    verify(downstream).forwardAgent(any());
  }

  @Test
  void emptyMessageIs400() throws Exception {
    mockMvc
        .perform(
            post("/v1/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"\"}"))
        .andExpect(status().isBadRequest());
  }
}
