package com.alex_star.systemofsearch.controller;

import com.alex_star.systemofsearch.dto.response.ResultResponseDto;
import com.alex_star.systemofsearch.model.Request;
import com.alex_star.systemofsearch.service.LemmatizerService;
import com.alex_star.systemofsearch.service.web.WebSearchService;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class SearchController {

    private final WebSearchService search;
    private final LemmatizerService lemmatizerService;
    private static final Log log = LogFactory.getLog(SearchController.class);

    public SearchController(WebSearchService search, LemmatizerService lemmatizerService) {
        this.search = search;
        this.lemmatizerService = lemmatizerService;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestParam(name = "query", required = false, defaultValue = "") String query,
            @RequestParam(name = "site", required = false, defaultValue = "") String site,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "limit", required = false, defaultValue = "0") int limit) {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            ArrayList<String> lemmas = lemmatizerService.getLemmas(query);
            ResultResponseDto service = search.getResponse(new Request(lemmas, query), site, offset, limit);
            stopWatch.stop();
            log.info("Search time " + stopWatch.getTotalTimeMillis() + " ms");
            return ResponseEntity.ok(service);
        } catch (Exception e) {
            System.out.println("ошибка морфологочиского анализа");
            return ResponseEntity.internalServerError().build();
        }
    }
}
