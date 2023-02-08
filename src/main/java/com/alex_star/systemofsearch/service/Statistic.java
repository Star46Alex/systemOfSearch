package com.alex_star.systemofsearch.service;

import com.alex_star.systemofsearch.dto.indexResponseEntity.Detailed;
import com.alex_star.systemofsearch.dto.indexResponseEntity.Statistics;
import com.alex_star.systemofsearch.dto.indexResponseEntity.Total;
import com.alex_star.systemofsearch.dto.response.StatisticResponse;
import com.alex_star.systemofsearch.model.Site;
import com.alex_star.systemofsearch.model.Status;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class Statistic  {

  private static final Log log = LogFactory.getLog(Statistic.class);
  private final SiteRepositoryService siteRepositoryService;
  private final LemmaRepositoryService lemmaRepositoryService;
  private final PageRepositoryService pageRepositoryService;

  public Statistic(SiteRepositoryService siteRepositoryService,
      LemmaRepositoryService lemmaRepositoryService,
      PageRepositoryService pageRepositoryService) {
    this.siteRepositoryService = siteRepositoryService;
    this.lemmaRepositoryService = lemmaRepositoryService;
    this.pageRepositoryService = pageRepositoryService;
  }

  public StatisticResponse getStatistic() {
    Total total = getTotal();
    List<Site> siteList = siteRepositoryService.getAllSites();
    Detailed[] detaileds = new Detailed[siteList.size()];
    for (int i = 0; i < siteList.size(); i++) {
      detaileds[i] = getDetailed(siteList.get(i));
    }
    log.info("Получение статистики.");
    return new StatisticResponse(true, new Statistics(total, detaileds));
  }

  private Total getTotal() {
    long sites = siteRepositoryService.siteCount();
    long lemmas = lemmaRepositoryService.lemmaCount();
    long pages = pageRepositoryService.pageCount();
    boolean isIndexing = isSitesIndexing();
    return new Total(sites, pages, lemmas, isIndexing);

  }

  private Detailed getDetailed(Site site) {
    String url = site.getUrl();
    String name = site.getName();
    Status status = site.getStatus();
    var statusTime = site.getStatusTime();
    long time = 0;
    if (statusTime != null)
      time = statusTime.getTime();
    String error = site.getLastError();
    long pages = pageRepositoryService.pageCount(site.getId());
    long lemmas = lemmaRepositoryService.lemmaCount(site.getId());
    return new Detailed(url, name, status, time, error, pages, lemmas);
  }

  private boolean isSitesIndexing() {
    boolean is = false;
    for (Site s : siteRepositoryService.getAllSites()) {
      if (s.getStatus().equals(Status.INDEXING)) {
        is = true;
        break;
      }
    }
    return is;
  }
}

