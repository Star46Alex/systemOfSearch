package com.alex_star.systemofsearch.siteCrawlingSystem;

import com.alex_star.systemofsearch.util.SiteIndexing;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import static com.alex_star.systemofsearch.siteCrawlingSystem.LinkPull.isInterrupted;

public class AllLinks {

  private final String url;
  private List<String> allLinks;

  public void builtAllLinks(SiteIndexing siteIndexing) {
    String text = new ForkJoinPool().invoke(new LinkPull(url, url, isInterrupted, siteIndexing));
    allLinks = toList(text);
  }

  private List<String> toList(String text) {
    return Arrays.stream(text.split("\n")).collect(Collectors.toList());
  }

  public List<String> getAllLinks() {
    return allLinks;
  }


  public AllLinks(String url) {
    this.url = url;
  }
}
