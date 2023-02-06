package com.alex_star.systemofsearch.service;

import com.alex_star.systemofsearch.dto.response.FalseResponse;
import com.alex_star.systemofsearch.dto.response.ResponseService;
import com.alex_star.systemofsearch.model.Request;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

@Service
public class SystemOfSearchService {

  private static final Log log = LogFactory.getLog(SystemOfSearchService.class);

  private final SystemOfSearch systemOfSearch;

  public SystemOfSearchService(SystemOfSearch systemOfSearch) {
    this.systemOfSearch = systemOfSearch;

  }

  ResponseService response;


  public ResponseService getResponse(Request request, String url, int offset, int limit) {
    log.info("Запрос на поиск строки- \"" + request.getRequest() + "\"");
    if (request.getRequest().equals("")) {
      response = new FalseResponse("Задан пустой поисковый запрос");
      return response;
    }
    if (url.equals("")) {
      response = systemOfSearch.searchResult(request, null, offset, limit);
    } else {
      response = systemOfSearch.searchResult(request, url, offset, limit);
    }
    if (response.getResult()) {
      log.info("Запрос на поиск строки обработан, результат получен.");
      return response;
    } else {
      log.warn("Запрос на поиск строки обработан, указанная страница не найдена.");
      return new FalseResponse("Указанная страница не найдена");
    }
  }
}


