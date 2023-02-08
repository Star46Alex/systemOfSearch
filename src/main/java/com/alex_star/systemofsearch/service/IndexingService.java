package com.alex_star.systemofsearch.service;

import com.alex_star.systemofsearch.dto.response.FalseResponse;
import com.alex_star.systemofsearch.dto.response.ResponseService;
import com.alex_star.systemofsearch.dto.response.TrueResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

@Service
public class IndexingService {

  private final Index index;

  private static final Log log = LogFactory.getLog(IndexingService.class);

  public IndexingService(Index index) {
    this.index = index;
  }


  public ResponseService startIndexingAll() {
    ResponseService response;
    boolean indexing;
    try {
      indexing = this.index.IndexAllSites();
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
    boolean indexing = this.index.stopSiteIndexing();
    ResponseService response;
    if (indexing) {
      response = new TrueResponse();
      log.info("Индексация остановлена");
    } 
    else {
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
      response = index.checkedSiteIndexing(url);
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
