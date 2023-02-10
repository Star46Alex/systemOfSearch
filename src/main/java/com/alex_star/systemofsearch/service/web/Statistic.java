package com.alex_star.systemofsearch.service.web;

import com.alex_star.systemofsearch.dto.indexResponseEntity.Detailed;
import com.alex_star.systemofsearch.dto.indexResponseEntity.Statistics;
import com.alex_star.systemofsearch.dto.indexResponseEntity.Total;
import com.alex_star.systemofsearch.dto.response.StatisticResponse;
import com.alex_star.systemofsearch.model.Site;
import com.alex_star.systemofsearch.model.Status;
import com.alex_star.systemofsearch.service.LemmaService;
import com.alex_star.systemofsearch.service.PageService;
import com.alex_star.systemofsearch.service.SiteService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class Statistic  {

  private static final Log log = LogFactory.getLog(Statistic.class);
  private final SiteService siteService;
  private final LemmaService lemmaService;
  private final PageService pageService;

  public Statistic(SiteService siteService,
      LemmaService lemmaService,
      PageService pageService) {
    this.siteService = siteService;
    this.lemmaService = lemmaService;
    this.pageService = pageService;
  }

  public StatisticResponse getStatistic() {
    Total total = getTotal();
    List<Site> siteList = siteService.getAllSites();
    Detailed[] detaileds = new Detailed[siteList.size()];
    for (int i = 0; i < siteList.size(); i++) {
      detaileds[i] = getDetailed(siteList.get(i));
    }
    log.info("Получение статистики.");
    return new StatisticResponse(true, new Statistics(total, detaileds));
  }

  private Total getTotal() {
    long sites = siteService.siteCount();
    long lemmas = lemmaService.lemmaCount();
    long pages = pageService.pageCount();
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
    long pages = pageService.pageCount(site.getId());
    long lemmas = lemmaService.lemmaCount(site.getId());
    return new Detailed(url, name, status, time, error, pages, lemmas);
  }

  private boolean isSitesIndexing() {
    boolean is = false;
    for (Site s : siteService.getAllSites()) {
      if (s.getStatus().equals(Status.INDEXING)) {
        is = true;
        break;
      }
    }
    return is;
  }
}

