package com.alex_star.systemofsearch.controller;

import com.alex_star.systemofsearch.dto.response.StatisticResponse;
import com.alex_star.systemofsearch.service.web.Statistic;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatisticController {

  private final Statistic statistic;

  public StatisticController(Statistic statistic) {
    this.statistic = statistic;
  }

  @GetMapping("/statistics")
  public ResponseEntity<Object> getStatistics() {
    StatisticResponse statistics = statistic.getStatistic();
    return ResponseEntity.ok(statistics);
  }
}