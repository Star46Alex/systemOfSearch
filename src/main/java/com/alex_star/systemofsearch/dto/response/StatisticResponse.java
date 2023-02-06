package com.alex_star.systemofsearch.dto.response;

import com.alex_star.systemofsearch.dto.indexResponseEntity.Statistics;


public class StatisticResponse implements ResponseService {

  boolean result;
  Statistics statistics;

  public StatisticResponse(boolean result, Statistics statistics) {
    this.result = result;
    this.statistics = statistics;
  }
  @Override
  public boolean getResult() {
    return result;
  }

  public void setResult(boolean result) {
    this.result = result;
  }

  public Statistics getStatistics() {
    return statistics;
  }

  public void setStatistics(Statistics statistics) {
    this.statistics = statistics;
  }
}


