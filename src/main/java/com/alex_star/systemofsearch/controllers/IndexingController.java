package com.alex_star.systemofsearch.controllers;

import com.alex_star.systemofsearch.dto.response.ResponseService;
import com.alex_star.systemofsearch.service.IndexingService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class IndexingController {

  private final IndexingService indexing;

  public IndexingController(IndexingService indexing) {
    this.indexing = indexing;
  }

  @GetMapping("/startIndexing")
  public ResponseEntity<Object> startIndexingAll() {
    ResponseService response = indexing.startIndexingAll();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/stopIndexing")
  public ResponseEntity<Object> stopIndexingAll() {
    ResponseService response = indexing.stopIndexing();
    return ResponseEntity.ok(response);
  }

  @PostMapping("/indexPage")
  public ResponseEntity<Object> startIndexingOne(
      @RequestParam(name = "url", required = false, defaultValue = " ") String url) {
    ResponseService response = indexing.startIndexingOne(url);
    return ResponseEntity.ok(response);
  }
}


