package com.alex_star.systemofsearch.util;

import com.alex_star.systemofsearch.config.Properties;
import com.alex_star.systemofsearch.lemmatizer.Lemmatizer;
import com.alex_star.systemofsearch.model.*;
import com.alex_star.systemofsearch.service.*;
import com.alex_star.systemofsearch.siteCrawlingSystem.AllLinks;
import com.alex_star.systemofsearch.siteCrawlingSystem.LinkPull;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.*;
public class SiteIndexing extends Thread {

    private final static Log log = LogFactory.getLog(SiteIndexing.class);
    private final Lemmatizer lemmatizer;
    private final Site site;
    private final Properties properties;
    private final FieldRepositoryService fieldRepositoryService;
    private final SiteRepositoryService siteRepositoryService;
    private final IndexRepositoryService indexRepositoryService;
    private final PageRepositoryService pageRepositoryService;
    private final LemmaRepositoryService lemmaRepositoryService;
    private final boolean allSite;

    public SiteIndexing(
        Site site, Properties properties,
        FieldRepositoryService fieldRepositoryService,
        SiteRepositoryService siteRepositoryService,
        IndexRepositoryService indexRepositoryService,
        PageRepositoryService pageRepositoryService,
        LemmaRepositoryService lemmaRepositoryService,
        boolean allSite) {
        this.site = site;
        this.allSite = allSite;
        this.lemmatizer = new Lemmatizer();
        this.properties = properties;
        this.fieldRepositoryService = fieldRepositoryService;
        this.siteRepositoryService = siteRepositoryService;
        this.indexRepositoryService = indexRepositoryService;
        this.pageRepositoryService = pageRepositoryService;
        this.lemmaRepositoryService = lemmaRepositoryService;
    }


    @Override
    public void run() {
        try {

            log.info("Собираем карту сайта " + site.getUrl());
            if (allSite) {
                runAllIndexing();
            } else {
                runOneSiteIndexing(site.getUrl());
            }
        } catch (InterruptedException e) {
            log.info("Остановка " + site.getUrl());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runAllIndexing() throws InterruptedException {
        site.setStatus(Status.INDEXING);
        site.setStatusTime(new Date());
        siteRepositoryService.save(site);
        AllLinks allLinks = new AllLinks(site.getUrl());
        allLinks.builtAllLinks(this);
        log.info("Карта сайта готова: " + site.getUrl());
        log.info("Начало индексации " + site.getUrl());
        List<String> allSiteUrls = allLinks.getAllLinks();
        for (String url : allSiteUrls) {
            runOneSiteIndexing(url);
        }
    }

    public void runOneSiteIndexing(String searchUrl) throws InterruptedException {
        site.setStatus(Status.INDEXING);
        site.setStatusTime(new Date());
        siteRepositoryService.save(site);
        List<Field> fieldList = getFieldListFromDB();
        try {
            Page page = getSearchPage(searchUrl, site.getId());
            Page checkPage = pageRepositoryService.getPage(searchUrl);
            if (checkPage != null) {
                page.setId(checkPage.getId());
            }
            HashMap<String, Integer> map = new HashMap<>();
            HashMap<String, Float> indexing = new HashMap<>();
            for (Field field : fieldList) {
                String name = field.getName();
                float weight = field.getWeight();
                String stringByTeg = getStringByTeg(name, page.getContent());
                HashMap<String, Integer> tempMap = lemmatizer.lemmatize(stringByTeg);
                map.putAll(tempMap);
                indexing.putAll(indexingLemmas(tempMap, weight));
            }
            lemmaToDB(map, site.getId());
            pageToDb(page);
            indexingToDb(indexing, page.getPath());
            indexing.clear();
        } catch (UnsupportedMimeTypeException e) {
            site.setLastError("Формат страницы не поддерживается: " + searchUrl);
            site.setStatus(Status.FAILED);
        } catch (IOException e) {
            site.setLastError("Ошибка чтения страницы: " + searchUrl + "\n" + e.getMessage());
            site.setStatus(Status.FAILED);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            siteRepositoryService.save(site);
        }
        if (LinkPull.isInterrupted) {
            site.setStatus(Status.FAILED);
            throw new InterruptedException("Остановка!");
        } else {
            site.setStatus(Status.INDEXED);
        }

        siteRepositoryService.save(site);
    }


    private void pageToDb(Page page) {
        pageRepositoryService.save(page);
    }

    private Page getSearchPage(String url, int siteId) throws IOException {
        log.info("getSearchPage=" + url + ", siteId=" + siteId);
        Page page = new Page();
        Connection.Response response = Jsoup.connect(url)
            .userAgent(properties.getAgent())
            .referrer("https://www.google.com")
            .execute();
        String content = response.body();
        int code = response.statusCode();
        page.setCode(code);
        page.setPath(url);
        page.setContent(content);
        page.setSiteId(siteId);
        return page;
    }

    private List<Field> getFieldListFromDB() {
        List<Field> list = new ArrayList<>();
        Iterable<Field> iterable = fieldRepositoryService.getAllField();
        iterable.forEach(list::add);
        return list;
    }

    private String getStringByTeg(String teg, String html) {
        String string = "";
        Document document = Jsoup.parse(html);
        Elements elements = document.select(teg);
        StringBuilder builder = new StringBuilder();
        elements.forEach(element -> builder.append(element.text()).append(" "));
        if (!builder.isEmpty()) {
            string = builder.toString();
        }
        return string;
    }

    private void lemmaToDB(HashMap<String, Integer> lemmaMap, int siteId) {
        for (Map.Entry<String, Integer> lemma : lemmaMap.entrySet()) {
            String lemmaName = lemma.getKey();
            List<Lemma> lemma1 = lemmaRepositoryService.getLemma(lemmaName);
            Lemma lemma2 = lemma1.stream().
                filter(lemma3 -> lemma3.getSiteId() == siteId).
                findFirst().
                orElse(null);
            if (lemma2 == null) {
                Lemma newLemma = new Lemma(lemmaName, 1, siteId);
                lemmaRepositoryService.save(newLemma);
            } else {
                int count = lemma2.getFrequency();
                lemma2.setFrequency(++count);
                lemmaRepositoryService.save(lemma2);
            }
        }
    }

    private HashMap<String, Float> indexingLemmas(HashMap<String, Integer> lemmas, float weight) {
        HashMap<String, Float> map = new HashMap<>();
        for (Map.Entry<String, Integer> lemma : lemmas.entrySet()) {
            String name = lemma.getKey();
            float w;
            if (!map.containsKey(name)) {
                w = (float) lemma.getValue() * weight;
            } else {
                w = map.get(name) + ((float) lemma.getValue() * weight);
            }
            map.put(name, w);
        }
        return map;
    }

    private void indexingToDb(HashMap<String, Float> map, String path) {
        Page page = pageRepositoryService.getPage(path);
        int pathId = page.getId();
        int siteId = page.getSiteId();
        for (Map.Entry<String, Float> lemma : map.entrySet()) {

            String lemmaName = lemma.getKey();
            List<Lemma> lemma1 = lemmaRepositoryService.getLemma(lemmaName);
            for (Lemma l : lemma1) {
                if (l.getSiteId() == siteId) {
                    int lemmaId = l.getId();
                    Indexing indexing = new Indexing(pathId, lemmaId, lemma.getValue());
                    indexRepositoryService.save(indexing);
                }
            }
        }
    }
}
