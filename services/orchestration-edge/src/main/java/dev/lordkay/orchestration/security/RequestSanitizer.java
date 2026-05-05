package dev.lordkay.orchestration.security;

import dev.lordkay.orchestration.config.EdgeProperties;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class RequestSanitizer {
  private static final Pattern CONTROL_CHARS = Pattern.compile("[\\p{Cntrl}&&[^\r\n\t]]");
  private static final Pattern INJECTION_PATTERNS =
      Pattern.compile("(?i)(ignore\\s+all\\s+instructions|system\\s*prompt|developer\\s*message)");

  private final EdgeProperties properties;

  public RequestSanitizer(EdgeProperties properties) {
    this.properties = properties;
  }

  public void validate(String message) {
    if (message.length() > properties.getRequestGuard().getMaxMessageLength()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message_too_long");
    }
    if (CONTROL_CHARS.matcher(message).find()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid_characters");
    }
    if (INJECTION_PATTERNS.matcher(message).find()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "disallowed_input_pattern");
    }
  }
}
