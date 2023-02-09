package com.alex_star.systemofsearch.siteCrawlingSystem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.RecursiveTask;


public class LinkPull extends RecursiveTask<String> {

  private final String url;
  private final String startUrl;
  private boolean isInterrupted;
  public static final Set<String> allLinks = new HashSet<>();
  private final Set<LinkPull> subTask = new HashSet<>();

  public LinkPull(String url, String startUrl) {
    this.url = url.trim();
    this.startUrl = startUrl.trim();
    isInterrupted = false;
  }


  @Override
  protected String compute() {
    if (isInterrupted) {
      return "";
    }
    StringBuilder sb = new StringBuilder(url + "\n");
    try {
      Thread.sleep(200);
      Document doc = receiveDoc();
      if (doc != null) {
        findChildrenAndRunSubTasks(doc, subTask);
        for (LinkPull link : subTask) {
          sb.append(link.join());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
    return sb.toString();
  }

  public void interrupt() {
    isInterrupted = true;
    for (var child : subTask) {
      child.interrupt();
    }
  }

  private Document receiveDoc() {
    try {
      Thread.sleep(200);
      return Jsoup.connect(url)
          .maxBodySize(0)
          .userAgent(
              "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
          .referrer("https://www.google.com")
          .followRedirects(false)
          .get();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
    return null;
  }


  private void findChildrenAndRunSubTasks(Document doc, Set<LinkPull> subTask) {
    Elements elements;
    elements = doc.select("a");
    for (Element el : elements) {
      String attr = el.attr("abs:href").toLowerCase();
      if (!allLinks.contains(attr) && attr.startsWith(startUrl)
          && !url.equals(attr)
          && !attr.contains("#")
          && !attr.contains(".pdf")
          && !attr.contains(".jpg")
          && !attr.contains(".jpeg")
          && !attr.contains(".png")
          && !attr.contains(".gif")
          && !attr.contains("?")
          && !attr.contains("&")
          && !attr.contains(".eps")
          && !attr.contains(".doc")
          && !attr.contains(".xlsx")
          && !attr.contains(".xls")) {
        allLinks.add(attr);
        LinkPull linkExecutor = new LinkPull(attr, startUrl);
        linkExecutor.fork();
        subTask.add(linkExecutor);
      }
    }
  }
}

