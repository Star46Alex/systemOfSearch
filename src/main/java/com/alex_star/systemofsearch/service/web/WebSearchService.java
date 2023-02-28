package com.alex_star.systemofsearch.service.web;

import com.alex_star.systemofsearch.dto.response.FalseResultResponseDto;
import com.alex_star.systemofsearch.dto.response.ResultResponseDto;
import com.alex_star.systemofsearch.model.Request;
import com.alex_star.systemofsearch.service.SearchService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

@Service
public class WebSearchService {

    private static final Log log = LogFactory.getLog(WebSearchService.class);

    private final SearchService searchService;

    public WebSearchService(SearchService searchService) {
        this.searchService = searchService;

    }

    ResultResponseDto response;


    public ResultResponseDto getResponse(Request request, String url, int offset, int limit) {
        log.info("Запрос на поиск строки- \"" + request.getRequest() + "\"");
        if (request.getRequest().equals("")) {
            response = new FalseResultResponseDto("Задан пустой поисковый запрос");
            return response;
        }
        if (url.equals("")) {
            response = searchService.searchResult(request, null, offset, limit);
        } else {
            response = searchService.searchResult(request, url, offset, limit);
        }
        if (response.getResult()) {
            log.info("Запрос на поиск строки обработан, результат получен.");
            return response;
        } else {
            log.warn("Запрос на поиск строки обработан, указанная страница не найдена.");
            return new FalseResultResponseDto("Указанная страница не найдена");
        }
    }
}


