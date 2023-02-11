package com.alex_star.systemofsearch.controller;

import com.alex_star.systemofsearch.dto.response.StatisticResultResponse;
import com.alex_star.systemofsearch.service.web.WebStatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatisticController {

  private final WebStatisticsService webStatisticsService;

  public StatisticController(WebStatisticsService webStatisticsService) {
    this.webStatisticsService = webStatisticsService;
  }

  @GetMapping("/statistics")
  public ResponseEntity<Object> getStatistics() {
    StatisticResultResponse statistics = webStatisticsService.getStatistic();
    return ResponseEntity.ok(statistics);
  }
}