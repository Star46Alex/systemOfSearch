package com.alex_star.systemofsearch.dto.response;

import com.alex_star.systemofsearch.dto.statistics.StatisticsDto;


public class StatisticResultResponse implements ResultResponse {

  boolean result;
  StatisticsDto statisticsDto;

  public StatisticResultResponse(boolean result, StatisticsDto statisticsDto) {
    this.result = result;
    this.statisticsDto = statisticsDto;
  }
  @Override
  public boolean getResult() {
    return result;
  }

  public void setResult(boolean result) {
    this.result = result;
  }

  public StatisticsDto getStatistics() {
    return statisticsDto;
  }

  public void setStatistics(StatisticsDto statisticsDto) {
    this.statisticsDto = statisticsDto;
  }
}


