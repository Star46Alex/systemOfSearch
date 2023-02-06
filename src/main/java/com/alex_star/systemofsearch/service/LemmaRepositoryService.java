package com.alex_star.systemofsearch.service;

import com.alex_star.systemofsearch.model.Indexing;
import com.alex_star.systemofsearch.model.Lemma;
import com.alex_star.systemofsearch.repository.LemmaRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
      if (lemmas.size() == 0) {
        lemmas.add(new Lemma());
      }

    } catch (Exception e) {
      System.out.println(lemmaName);
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

  public synchronized void deleteAllLemmas(List<Lemma> lemmaList) {
    lemmaRepository.deleteAll(lemmaList);
  }


  public List<Lemma> findLemmasByIndexing(List<Indexing> indexingList) {
    int[] lemmaIdList = new int[indexingList.size()];
    for (int i = 0; i < indexingList.size(); i++) {
      lemmaIdList[i] = indexingList.get(i).getLemmaId();
    }
    return lemmaRepository.findSiteById(lemmaIdList);
  }

  public void sortLemmasByFrequency(HashMap<String, Integer> lemmas, int siteId)
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
