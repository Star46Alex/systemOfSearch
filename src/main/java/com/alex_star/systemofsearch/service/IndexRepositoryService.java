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


  public List<Indexing> getAllIndexingByPageId(int pageId) {
    return indexRepository.findByPageId(pageId);
  }


  public synchronized void deleteAllIndexing(List<Indexing> indexingList) {
    indexRepository.deleteAll(indexingList);
  }


  public Indexing getIndexing(int lemmaId, int pageId) {
    Indexing indexing = null;
    try {
      indexing = indexRepository.findByLemmaIdAndPageId(lemmaId, pageId);
    } catch (Exception e) {
      System.out.println("lemmaId: " + lemmaId + " + pageId: " + pageId + " not unique");
      e.printStackTrace();
    }
    return indexing;
  }

  public synchronized void save(Indexing indexing) {
    indexRepository.save(indexing);
  }

}

