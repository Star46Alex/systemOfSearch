package com.alex_star.systemofsearch.service.web;

import com.alex_star.systemofsearch.dto.response.FalseResponse;
import com.alex_star.systemofsearch.dto.response.ResponseService;
import com.alex_star.systemofsearch.dto.response.TrueResponse;
import com.alex_star.systemofsearch.service.IndexingService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

@Service
public class WebIndexingService {

  private final IndexingService indexingService;

  private static final Log log = LogFactory.getLog(WebIndexingService.class);

  public WebIndexingService(IndexingService indexingService) {
    this.indexingService = indexingService;
  }


  public ResponseService startIndexingAll() {
    ResponseService response;
    boolean indexing;
    try {
      indexing = this.indexingService.IndexAllSites();
      log.info("Попытка запуска индексации всех сайтов");
    } catch (InterruptedException e) {
      response = new FalseResponse("Ошибка запуска индексации");
      log.error("Ошибка запуска индексации", e);
      return response;
    }
    if (indexing) {
      response = new TrueResponse();
      log.info("Индексация всех сайтов запущена");
    } else {
      response = new FalseResponse("Индексация уже запущена");
      log.warn("Индексация всех сайтов не запущена. Т.к. процесс индексации был запущен ранее.");
    }
    return response;
  }


  public ResponseService stopIndexing() {
    log.info("Попытка остановки индексации");
    boolean indexing = this.indexingService.stopSiteIndexing();
    ResponseService response;
    if (indexing) {
      response = new TrueResponse();
      log.info("Индексация остановлена");
    } else {
      response = new FalseResponse("Индексация не запущена");
      log.warn(
          "Остановка индексации не может быть выполнена, потому что процесс индексации не запущен.");
    }
    return response;
  }


  public ResponseService startIndexingOne(String url) {
    ResponseService resp;
    String response;
    try {
      response = indexingService.checkedSiteIndexing(url);
    } catch (InterruptedException e) {
      resp = new FalseResponse("Ошибка запуска индексации");
      return resp;
    }
    if (response.equals("not found")) {
      resp = new FalseResponse("Страница находится за пределами сайтов," +
          " указанных в конфигурационном файле");
    } else if (response.equals("false")) {
      resp = new FalseResponse("Индексация страницы уже запущена");
    } else {
      resp = new TrueResponse();
    }
    return resp;
  }
}
