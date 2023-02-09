package com.alex_star.systemofsearch.service;

import com.alex_star.systemofsearch.model.Lemma;
import com.alex_star.systemofsearch.repository.LemmaRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service

public class LemmaRepositoryService {

  private final LemmaRepository lemmaRepository;


  public LemmaRepositoryService(LemmaRepository lemmaRepository) {
    this.lemmaRepository = lemmaRepository;
  }


  public List<Lemma> getLemma(String lemmaName) {
    List<Lemma> lemmas = null;
    try {
      lemmas = lemmaRepository.findSiteByLemma(lemmaName);
      if (lemmas == null) {
        lemmas = new ArrayList<>();
      }
      if (lemmas.isEmpty()) {
        lemmas.add(new Lemma());
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    return lemmas;
  }


  public synchronized void save(Lemma lemma) {
    lemmaRepository.save(lemma);
  }


  public long lemmaCount() {
    return lemmaRepository.count();
  }


  public long lemmaCount(long siteId) {
    return lemmaRepository.count(siteId);
  }


  public void sortLemmasByFrequency(Map<String, Integer> lemmas, int siteId)
      throws SQLException {
    for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
      String key = entry.getKey();

      int result = 0;

      if (key != null && !key.isEmpty()) {
        Lemma lemma = lemmaRepository.getLemmaByName(key, siteId);
        if (lemma != null) {
          result = lemma.getFrequency();
        }
        if (result != 0) {
          lemmas.replace(key, result);
        }
      }

    }
  }

}
