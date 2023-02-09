package com.alex_star.systemofsearch.siteCrawlingSystem;


import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class AllLinks {

  private final String url;
  private List<String> allLinks;
  private LinkPull linkPull;

  public void builtAllLinks() {
    linkPull = new LinkPull(url, url);
    String text = new ForkJoinPool().invoke(linkPull);
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

  public void interrupt()
  {
    linkPull.interrupt();
  }
}
