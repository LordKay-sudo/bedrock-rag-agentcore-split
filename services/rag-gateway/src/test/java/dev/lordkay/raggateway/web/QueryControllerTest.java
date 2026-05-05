package dev.lordkay.raggateway.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class QueryControllerTest {

  @Autowired MockMvc mockMvc;

  @Test
  void queryReturnsRagPathAndCitation() throws Exception {
    mockMvc
        .perform(
            post("/v1/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"What is the refund policy?\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.pathUsed").value("rag"))
        .andExpect(jsonPath("$.text").isString())
        .andExpect(jsonPath("$.citations[0].sourceId").value("stub-doc-1"));
  }

  @Test
  void preferredPathAgentReturnsAgentPathUsed() throws Exception {
    mockMvc
        .perform(
            post("/v1/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"x\",\"preferredPath\":\"agent\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.pathUsed").value("agent"));
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
