package com.alex_star.systemofsearch.controllers;

import com.alex_star.systemofsearch.dto.response.ResponseService;
import com.alex_star.systemofsearch.model.Request;
import com.alex_star.systemofsearch.service.SystemOfSearchService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class SearchController {

  private final SystemOfSearchService search;
  private static final Log log = LogFactory.getLog(SearchController.class);

  public SearchController(SystemOfSearchService search) {
    this.search = search;
  }

  @GetMapping("/search")
  public ResponseEntity<Object> search(
      @RequestParam(name = "query", required = false, defaultValue = "") String query,
      @RequestParam(name = "site", required = false, defaultValue = "") String site,
      @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
      @RequestParam(name = "limit", required = false, defaultValue = "0") int limit)
      {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ResponseService service = search.getResponse(new Request(query), site, offset, limit);
        stopWatch.stop();
        log.info("Search time " + stopWatch.getTotalTimeMillis() + " ms");
    return ResponseEntity.ok(service);
  }
}
