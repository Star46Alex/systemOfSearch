package com.alex_star.systemofsearch.dto.response;

public class FalseResponse implements ResponseService {

  private final String error;

  public FalseResponse(String error) {
    this.error = error;
  }

  @Override
  public boolean getResult() {
    return false;
  }

  public String getError() {
    return error;
  }
}
