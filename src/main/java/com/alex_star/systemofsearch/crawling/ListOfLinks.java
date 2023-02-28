package com.alex_star.systemofsearch.crawling;


import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class ListOfLinks {

    private final String url;
    private List<String> allLinks;
    private Crawler crawler;

    public void builtAllLinks() {
        crawler = new Crawler(url, url);
        String text = new ForkJoinPool().invoke(crawler);
        allLinks = toList(text);
    }

    private List<String> toList(String text) {
        return Arrays.stream(text.split("\n")).collect(Collectors.toList());
    }

    public List<String> getAllLinks() {
        return allLinks;
    }


    public ListOfLinks(String url) {
        this.url = url;
    }

    public void interrupt() {
        crawler.interrupt();
    }
}
