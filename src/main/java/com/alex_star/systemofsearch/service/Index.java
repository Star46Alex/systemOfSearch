package com.alex_star.systemofsearch.service;

import com.alex_star.systemofsearch.config.Properties;
import com.alex_star.systemofsearch.model.Field;
import com.alex_star.systemofsearch.model.Site;
import com.alex_star.systemofsearch.model.Status;
import com.alex_star.systemofsearch.siteCrawlingSystem.LinkPull;
import com.alex_star.systemofsearch.util.SiteIndexing;
import java.util.Vector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class Index {

  private static final Log log = LogFactory.getLog(Index.class);
  private final Properties properties;
  private final FieldRepositoryService fieldRepositoryService;
  private final SiteRepositoryService siteRepositoryService;
  private final IndexingRepositoryService indexingRepositoryService;
  private final PageRepositoryService pageRepositoryService;
  private final LemmaRepositoryService lemmaRepositoryService;
  private final List<SiteIndexing> siteIndexings;

  public Index(Properties properties,
      FieldRepositoryService fieldRepositoryService,
      SiteRepositoryService siteRepositoryService,
      IndexingRepositoryService indexingRepositoryService,
      PageRepositoryService pageRepositoryService,
      LemmaRepositoryService lemmaRepositoryService) {
    this.properties = properties;
    this.fieldRepositoryService = fieldRepositoryService;
    this.siteRepositoryService = siteRepositoryService;
    this.indexingRepositoryService = indexingRepositoryService;
    this.pageRepositoryService = pageRepositoryService;
    this.lemmaRepositoryService = lemmaRepositoryService;
    this.siteIndexings = new Vector<>();
  }

  ThreadPoolExecutor executor;

  public boolean IndexAllSites() throws InterruptedException {
    LinkPull.allLinks.clear();
    List<Site> siteList = getSiteList();
    executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(siteList.size());
    fieldInit();
    boolean isIndexing;
    for (Site site : siteList) {
      isIndexing = startSiteIndexing(site);
      if (!isIndexing) {
        stopSiteIndexing();
        return false;
      }
    }
    return true;
  }

  public String checkedSiteIndexing(String url) throws InterruptedException {
    List<Site> siteList = siteRepositoryService.getAllSites();
    String baseUrl = "";
    for (Site site : siteList) {
      if (site.getStatus() != Status.INDEXED) {
        return "false";
      }
      if (url.contains(site.getUrl())) {
        baseUrl = site.getUrl();
      }
    }
    if (baseUrl.isEmpty()) {
      return "not found";
    } else {
      Site site = siteRepositoryService.getSite(baseUrl);
      site.setUrl(url);
      SiteIndexing indexing = new SiteIndexing(
          site,
          properties,
          fieldRepositoryService,
          siteRepositoryService,
          indexingRepositoryService,
          pageRepositoryService,
          lemmaRepositoryService,
          false);
      executor.execute(indexing);
      site.setUrl(baseUrl);
      siteRepositoryService.save(site);
      return "true";
    }
  }


  private void fieldInit() {
    Field fieldTitle = new Field("title", "title", 1.0f);
    Field fieldBody = new Field("body", "body", 0.8f);
    if (fieldRepositoryService.getFieldByName("title") == null) {
      fieldRepositoryService.save(fieldTitle);
      fieldRepositoryService.save(fieldBody);
    }
  }

  private boolean startSiteIndexing(Site site) {
    Site site1 = siteRepositoryService.getSite(site.getUrl());
    if (site1 == null) {
      siteRepositoryService.save(site);
      SiteIndexing siteIndexing = new SiteIndexing(
          siteRepositoryService.getSite(site.getUrl()),
          properties,
          fieldRepositoryService,
          siteRepositoryService,
          indexingRepositoryService,
          pageRepositoryService,
          lemmaRepositoryService,
          true);
      executor.execute(siteIndexing);
      siteIndexings.add(siteIndexing);
      return true;
    } else {
      if (!site1.getStatus().equals(Status.INDEXING)) {
        SiteIndexing  siteIndexing= new SiteIndexing(
            siteRepositoryService.getSite(site.getUrl()),
            properties,
            fieldRepositoryService,
            siteRepositoryService,
            indexingRepositoryService,
            pageRepositoryService,
            lemmaRepositoryService,
            true);
        executor.execute(siteIndexing);
        siteIndexings.add(siteIndexing);
        return true;
      } else {
        return false;
      }
    }
  }

  public boolean stopSiteIndexing() {
    boolean isThreadAlive = false;
    if (executor.getActiveCount() == 0) {
      return false;
    }
    for(SiteIndexing si:siteIndexings)
    {
      si.interrupt();
    }
    executor.shutdownNow();
    try {
      isThreadAlive = executor.awaitTermination(1, TimeUnit.MINUTES);
    } catch (InterruptedException e) {
      log.error("Ошибка закрытия потоков: " + e);
    }
    if (isThreadAlive) {
      List<Site> siteList = siteRepositoryService.getAllSites();
      for (Site site : siteList) {
        site.setStatus(Status.FAILED);
        siteRepositoryService.save(site);
      }
    }
    return isThreadAlive;
  }

  private List<Site> getSiteList() {
    List<Site> siteList = new ArrayList<>();
    List<HashMap<String, String>> sites = properties.getSite();
    for (HashMap<String, String> map : sites) {
      String url = "";
      String name = "";
      for (Map.Entry<String, String> siteInfo : map.entrySet()) {
        if (siteInfo.getKey().equals("name")) {
          name = siteInfo.getValue();
        }
        if (siteInfo.getKey().equals("url")) {
          url = siteInfo.getValue();
        }
      }
      Site site = new Site();
      site.setUrl(url);
      site.setName(name);
      site.setStatus(Status.FAILED);
      siteList.add(site);
    }
    return siteList;
  }

}

