package com.alex_star.systemofsearch.service;

import com.alex_star.systemofsearch.config.Properties;
import com.alex_star.systemofsearch.model.Field;
import com.alex_star.systemofsearch.model.Indexing;
import com.alex_star.systemofsearch.model.Site;
import com.alex_star.systemofsearch.model.Status;
import com.alex_star.systemofsearch.repository.IndexingRepository;
import com.alex_star.systemofsearch.crawling.LinkPull;
import com.alex_star.systemofsearch.crawling.SiteIndexing;
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
public class indexingService {

  private static final Log log = LogFactory.getLog(indexingService.class);
  private final Properties properties;
  private final LemmatizerService lemmatizerService;
  private final FieldService fieldService;
  private final SiteService siteService;
  private final IndexingRepository indexingRepository;
  private final PageService pageService;
  private final LemmaService lemmaService;
  private final List<SiteIndexing> siteIndexings;

  public indexingService(Properties properties,
      LemmatizerService lemmatizerService, FieldService fieldService,
      SiteService siteService,
      IndexingRepository indexingRepository, PageService pageService,
      LemmaService lemmaService) {
    this.properties = properties;
    this.lemmatizerService = lemmatizerService;
    this.fieldService = fieldService;
    this.siteService = siteService;
    this.indexingRepository = indexingRepository;
    this.pageService = pageService;
    this.lemmaService = lemmaService;
    this.siteIndexings = new Vector<>();
  }

  ThreadPoolExecutor executor;

  public boolean indexAllSites() throws InterruptedException {
    if(executor!=null) {
      throw new IllegalStateException("Индексация уже запущена!");
    }
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
    List<Site> siteList = siteService.getAllSites();
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
      Site site = siteService.getSite(baseUrl);
      site.setUrl(url);
      SiteIndexing indexing = new SiteIndexing(
          site,
          properties,
          fieldService,
          siteService,
          indexingRepository,
          pageService,
          lemmaService,
          lemmatizerService,
          false);
      executor.execute(indexing);
      site.setUrl(baseUrl);
      siteService.save(site);
      return "true";
    }
  }


  private void fieldInit() {
    Field fieldTitle = new Field("title", "title", 1.0f);
    Field fieldBody = new Field("body", "body", 0.8f);
    if (fieldService.getFieldByName("title") == null) {
      fieldService.save(fieldTitle);
      fieldService.save(fieldBody);
    }
  }

  private boolean startSiteIndexing(Site site) {
    Site site1 = siteService.getSite(site.getUrl());
    if (site1 == null) {
      siteService.save(site);
      SiteIndexing siteIndexing = new SiteIndexing(
          siteService.getSite(site.getUrl()),
          properties,
          fieldService,
          siteService,
          indexingRepository,
          pageService,
          lemmaService,
          lemmatizerService,
          true);
      executor.execute(siteIndexing);
      siteIndexings.add(siteIndexing);
      return true;
    } else {
      if (!site1.getStatus().equals(Status.INDEXING)) {
        SiteIndexing  siteIndexing= new SiteIndexing(
            siteService.getSite(site.getUrl()),
            properties,
            fieldService,
            siteService,
            indexingRepository,
            pageService,
            lemmaService,
            lemmatizerService,
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
      List<Site> siteList = siteService.getAllSites();
      for (Site site : siteList) {
        site.setStatus(Status.FAILED);
        siteService.save(site);
      }
    }
    executor=null;
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

  public List<Indexing> getAllIndexingByLemmaId(int lemmaId) {
    return indexingRepository.findByLemmaId(lemmaId);
  }


  public synchronized void save(Indexing indexing) {
    indexingRepository.save(indexing);
  }

}

