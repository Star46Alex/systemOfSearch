package com.alex_star.systemofsearch.service.web;

import com.alex_star.systemofsearch.dto.response.FalseResultResponse;
import com.alex_star.systemofsearch.dto.response.ResultResponse;
import com.alex_star.systemofsearch.dto.response.TrueResultResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

@Service
public class WebIndexingService {

  private final com.alex_star.systemofsearch.service.indexingService indexingService;

  private static final Log log = LogFactory.getLog(WebIndexingService.class);

  public WebIndexingService(com.alex_star.systemofsearch.service.indexingService indexingService) {
    this.indexingService = indexingService;
  }


  public ResultResponse startIndexingAll() {
    ResultResponse response;
    boolean indexing;
    try {
      indexing = this.indexingService.indexAllSites();
      log.info("Попытка запуска индексации всех сайтов");
    } catch (InterruptedException e) {
      response = new FalseResultResponse("Ошибка запуска индексации");
      log.error("Ошибка запуска индексации", e);
      return response;
    }
    if (indexing) {
      response = new TrueResultResponse();
      log.info("Индексация всех сайтов запущена");
    } else {
      response = new FalseResultResponse("Индексация уже запущена");
      log.warn("Индексация всех сайтов не запущена. Т.к. процесс индексации был запущен ранее.");
    }
    return response;
  }


  public ResultResponse stopIndexing() {
    log.info("Попытка остановки индексации");
    boolean indexing = this.indexingService.stopSiteIndexing();
    ResultResponse response;
    if (indexing) {
      response = new TrueResultResponse();
      log.info("Индексация остановлена");
    } else {
      response = new FalseResultResponse("Индексация не запущена");
      log.warn(
          "Остановка индексации не может быть выполнена, потому что процесс индексации не запущен.");
    }
    return response;
  }


  public ResultResponse startIndexingOne(String url) {
    ResultResponse resp;
    String response;
    try {
      response = indexingService.checkedSiteIndexing(url);
    } catch (InterruptedException e) {
      resp = new FalseResultResponse("Ошибка запуска индексации");
      return resp;
    }
    if (response.equals("not found")) {
      resp = new FalseResultResponse("Страница находится за пределами сайтов," +
          " указанных в конфигурационном файле");
    } else if (response.equals("false")) {
      resp = new FalseResultResponse("Индексация страницы уже запущена");
    } else {
      resp = new TrueResultResponse();
    }
    return resp;
  }
}
