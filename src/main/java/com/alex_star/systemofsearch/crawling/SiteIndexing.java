package com.alex_star.systemofsearch.crawling;

import com.alex_star.systemofsearch.config.Properties;
import com.alex_star.systemofsearch.service.LemmatizerService;
import com.alex_star.systemofsearch.model.*;
import com.alex_star.systemofsearch.repository.IndexingRepository;
import com.alex_star.systemofsearch.service.*;
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
    private final LemmatizerService lemmatizerService;
    private final Site site;
    private final Properties properties;
    private final FieldService fieldService;
    private final SiteService siteService;
    private final IndexingRepository indexingRepository;
    private final PageService pageService;
    private final LemmaService lemmaService;
    private final boolean allSite;
    private ListOfLinks listOfLinks;

    public SiteIndexing(
            Site site, Properties properties,
            FieldService fieldService,
            SiteService siteService,
            IndexingRepository indexingRepository, PageService pageService,
            LemmaService lemmaService, LemmatizerService lemmatizerService,
            boolean allSite) {
        this.site = site;
        this.indexingRepository = indexingRepository;
        this.allSite = allSite;
        this.lemmatizerService = lemmatizerService;
        this.properties = properties;
        this.fieldService = fieldService;
        this.siteService = siteService;
        this.pageService = pageService;
        this.lemmaService = lemmaService;
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

    @Override
    public void interrupt() {
        listOfLinks.interrupt();
        super.interrupt();
    }

    public void runAllIndexing() throws InterruptedException {
        site.setStatus(Status.INDEXING);
        site.setStatusTime(new Date());
        siteService.save(site);
        listOfLinks = new ListOfLinks(site.getUrl());
        listOfLinks.builtAllLinks();
        log.info("Карта сайта готова: " + site.getUrl());
        log.info("Начало индексации " + site.getUrl());
        List<String> allSiteUrls = listOfLinks.getAllLinks();
        for (String url : allSiteUrls) {
            runOneSiteIndexing(url);
        }
    }


    public void runOneSiteIndexing(String searchUrl) throws InterruptedException {
        site.setStatus(Status.INDEXING);
        site.setStatusTime(new Date());
        siteService.save(site);
        List<Field> fieldList = getFieldListFromDB();
        try {
            Page page = getSearchPage(searchUrl, site.getId());
            Page checkPage = pageService.getPage(searchUrl);
            if (checkPage != null) {
                page.setId(checkPage.getId());
            }
            HashMap<String, Integer> map = new HashMap<>();
            HashMap<String, Float> indexing = new HashMap<>();
            for (Field field : fieldList) {
                String name = field.getName();
                float weight = field.getWeight();
                String stringByTeg = getStringByTeg(name, page.getContent());
                HashMap<String, Integer> tempMap = lemmatizerService.lemmatizer(stringByTeg);
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
            siteService.save(site);
        }
        if (isInterrupted()) {
            site.setStatus(Status.FAILED);
            throw new InterruptedException("Остановка!");
        } else {
            site.setStatus(Status.INDEXED);
        }

        siteService.save(site);
    }


    private void pageToDb(Page page) {
        pageService.save(page);
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
        Iterable<Field> iterable = fieldService.getAllField();
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
            List<Lemma> lemma1 = lemmaService.getLemma(lemmaName);
            Lemma lemma2 = lemma1.stream().
                    filter(lemma3 -> lemma3.getSiteId() == siteId).
                    findFirst().
                    orElse(null);
            if (lemma2 == null) {
                Lemma newLemma = new Lemma(lemmaName, 1, siteId);
                lemmaService.save(newLemma);
            } else {
                int count = lemma2.getFrequency();
                lemma2.setFrequency(++count);
                lemmaService.save(lemma2);
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
        Page page = pageService.getPage(path);
        int pathId = page.getId();
        int siteId = page.getSiteId();
        for (Map.Entry<String, Float> lemma : map.entrySet()) {

            String lemmaName = lemma.getKey();
            List<Lemma> lemma1 = lemmaService.getLemma(lemmaName);
            for (Lemma l : lemma1) {
                if (l.getSiteId() == siteId) {
                    int lemmaId = l.getId();
                    Indexing indexing = new Indexing(pathId, lemmaId, lemma.getValue());
                    indexingRepository.save(indexing);
                }
            }
        }
    }
}
