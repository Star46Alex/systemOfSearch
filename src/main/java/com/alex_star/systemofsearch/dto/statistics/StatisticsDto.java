package com.alex_star.systemofsearch.dto.statistics;

public class StatisticsDto {

  TotalDto totalDto;
  DetailedDto[] detailedDto;

  public StatisticsDto(TotalDto totalDto, DetailedDto[] detailedDto) {
    this.totalDto = totalDto;
    this.detailedDto = detailedDto;
  }

  public TotalDto getTotal() {
    return totalDto;
  }

  public void setTotal(TotalDto totalDto) {
    this.totalDto = totalDto;
  }

  public DetailedDto[] getDetailed() {
    return detailedDto;
  }

  public void setDetailed(DetailedDto[] detailedDto) {
    this.detailedDto = detailedDto;
  }
}

