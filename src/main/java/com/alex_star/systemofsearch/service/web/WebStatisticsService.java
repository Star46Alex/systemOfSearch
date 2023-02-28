package com.alex_star.systemofsearch.service.web;

import com.alex_star.systemofsearch.dto.statistics.DetailedDto;
import com.alex_star.systemofsearch.dto.statistics.StatisticsDto;
import com.alex_star.systemofsearch.dto.statistics.TotalDto;
import com.alex_star.systemofsearch.dto.response.StatisticResultResponseDto;
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
public class WebStatisticsService {

    private static final Log log = LogFactory.getLog(WebStatisticsService.class);
    private final SiteService siteService;
    private final LemmaService lemmaService;
    private final PageService pageService;

    public WebStatisticsService(SiteService siteService,
                                LemmaService lemmaService,
                                PageService pageService) {
        this.siteService = siteService;
        this.lemmaService = lemmaService;
        this.pageService = pageService;
    }

    public StatisticResultResponseDto getStatistic() {
        TotalDto totalDto = getTotal();
        List<Site> siteList = siteService.getAllSites();
        DetailedDto[] detailedDtos = new DetailedDto[siteList.size()];
        for (int i = 0; i < siteList.size(); i++) {
            detailedDtos[i] = getDetailed(siteList.get(i));
        }
        log.info("Получение статистики.");
        return new StatisticResultResponseDto(true, new StatisticsDto(totalDto, detailedDtos));
    }

    private TotalDto getTotal() {
        long sites = siteService.siteCount();
        long lemmas = lemmaService.lemmaCount();
        long pages = pageService.pageCount();
        boolean isIndexing = isSitesIndexing();
        return new TotalDto(sites, pages, lemmas, isIndexing);

    }

    private DetailedDto getDetailed(Site site) {
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
        return new DetailedDto(url, name, status, time, error, pages, lemmas);
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

