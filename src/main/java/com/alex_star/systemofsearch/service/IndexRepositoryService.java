package com.alex_star.systemofsearch.service;

import com.alex_star.systemofsearch.model.Indexing;
import com.alex_star.systemofsearch.repository.IndexRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndexRepositoryService {

  private final IndexRepository indexRepository;

  public IndexRepositoryService(IndexRepository indexRepository) {
    this.indexRepository = indexRepository;
  }


  public List<Indexing> getAllIndexingByLemmaId(int lemmaId) {
    return indexRepository.findByLemmaId(lemmaId);
  }


  public synchronized void save(Indexing indexing) {
    indexRepository.save(indexing);
  }

}

