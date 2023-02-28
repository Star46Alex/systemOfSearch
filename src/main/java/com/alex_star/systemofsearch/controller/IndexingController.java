package com.alex_star.systemofsearch.controller;

import com.alex_star.systemofsearch.dto.response.ResultResponseDto;
import com.alex_star.systemofsearch.service.web.WebIndexingService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class IndexingController {

    private final WebIndexingService indexing;

    public IndexingController(WebIndexingService indexing) {
        this.indexing = indexing;
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<Object> startIndexingAll() {
        ResultResponseDto response = indexing.startIndexingAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<Object> stopIndexingAll() {
        ResultResponseDto response = indexing.stopIndexing();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/indexPage")
    public ResponseEntity<Object> startIndexingOne(
            @RequestParam(name = "url", required = false, defaultValue = " ") String url) {
        ResultResponseDto response = indexing.startIndexingOne(url);
        return ResponseEntity.ok(response);
    }
}


