package com.alex_star.systemofsearch.service;

import com.alex_star.systemofsearch.model.Indexing;
import com.alex_star.systemofsearch.repository.IndexingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndexingRepositoryService {

  private final IndexingRepository indexingRepository;

  public IndexingRepositoryService(IndexingRepository indexingRepository) {
    this.indexingRepository = indexingRepository;
  }


  public List<Indexing> getAllIndexingByLemmaId(int lemmaId) {
    return indexingRepository.findByLemmaId(lemmaId);
  }


  public synchronized void save(Indexing indexing) {
    indexingRepository.save(indexing);
  }

}

