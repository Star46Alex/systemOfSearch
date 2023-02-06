package com.alex_star.systemofsearch.service;

import com.alex_star.systemofsearch.model.ProcessingLinks;
import com.alex_star.systemofsearch.repository.ProcessingLinksRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcessingLinksService {

  private final ProcessingLinksRepository processingLinksRepository;

  public ProcessingLinksService(ProcessingLinksRepository processingLinksRepository) {
    this.processingLinksRepository = processingLinksRepository;
  }

  public synchronized List<ProcessingLinks> findProcessingLinksBySiteUrl(String siteUrl) {
    return processingLinksRepository.findProcessingLinksBySiteUrl(siteUrl);
  }

  public ProcessingLinks save(ProcessingLinks processingLinks) {
    return processingLinksRepository.save(processingLinks);
  }

}
