package dev.lordkay.orchestration.downstream;

public class DownstreamQueryException extends RuntimeException {

  private final String downstream;
  private final int status;

  public DownstreamQueryException(String downstream, int status, Throwable cause) {
    super(cause);
    this.downstream = downstream;
    this.status = status;
  }

  public String getDownstream() {
    return downstream;
  }

  public int getStatus() {
    return status;
  }
}
