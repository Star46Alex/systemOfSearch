package com.alex_star.systemofsearch.controller;

import com.alex_star.systemofsearch.dto.response.StatisticResponse;
import com.alex_star.systemofsearch.service.web.WebStatisticService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatisticController {

  private final WebStatisticService webStatisticService;

  public StatisticController(WebStatisticService webStatisticService) {
    this.webStatisticService = webStatisticService;
  }

  @GetMapping("/statistics")
  public ResponseEntity<Object> getStatistics() {
    StatisticResponse statistics = webStatisticService.getStatistic();
    return ResponseEntity.ok(statistics);
  }
}