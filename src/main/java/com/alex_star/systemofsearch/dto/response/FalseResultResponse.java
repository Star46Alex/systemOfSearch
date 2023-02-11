package com.alex_star.systemofsearch.dto.response;

public class FalseResultResponse implements ResultResponse {

  private final String error;

  public FalseResultResponse(String error) {
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
