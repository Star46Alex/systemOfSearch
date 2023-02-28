package com.alex_star.systemofsearch.service.web;

import com.alex_star.systemofsearch.dto.response.FalseResultResponseDto;
import com.alex_star.systemofsearch.dto.response.ResultResponseDto;
import com.alex_star.systemofsearch.dto.response.TrueResultResponseDto;
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


    public ResultResponseDto startIndexingAll() {
        ResultResponseDto response;
        boolean indexing;
        try {
            indexing = this.indexingService.indexAllSites();
            log.info("Попытка запуска индексации всех сайтов");
        } catch (InterruptedException e) {
            response = new FalseResultResponseDto("Ошибка запуска индексации");
            log.error("Ошибка запуска индексации", e);
            return response;
        }
        if (indexing) {
            response = new TrueResultResponseDto();
            log.info("Индексация всех сайтов запущена");
        } else {
            response = new FalseResultResponseDto("Индексация уже запущена");
            log.warn("Индексация всех сайтов не запущена. Т.к. процесс индексации был запущен ранее.");
        }
        return response;
    }


    public ResultResponseDto stopIndexing() {
        log.info("Попытка остановки индексации");
        boolean indexing = this.indexingService.stopSiteIndexing();
        ResultResponseDto response;
        if (indexing) {
            response = new TrueResultResponseDto();
            log.info("Индексация остановлена");
        } else {
            response = new FalseResultResponseDto("Индексация не запущена");
            log.warn(
                    "Остановка индексации не может быть выполнена, потому что процесс индексации не запущен.");
        }
        return response;
    }


    public ResultResponseDto startIndexingOne(String url) {
        ResultResponseDto resp;
        String response;
        try {
            response = indexingService.checkedSiteIndexing(url);
        } catch (InterruptedException e) {
            resp = new FalseResultResponseDto("Ошибка запуска индексации");
            return resp;
        }
        if (response.equals("not found")) {
            resp = new FalseResultResponseDto("Страница находится за пределами сайтов," +
                    " указанных в конфигурационном файле");
        } else if (response.equals("false")) {
            resp = new FalseResultResponseDto("Индексация страницы уже запущена");
        } else {
            resp = new TrueResultResponseDto();
        }
        return resp;
    }
}
