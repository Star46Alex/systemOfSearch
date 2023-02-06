package com.alex_star.systemofsearch.siteCrawlingSystem;

import com.alex_star.systemofsearch.util.SiteIndexing;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
  private String content = "";
  private final String startUrl;
  public static boolean isInterrupted;

  private final SiteIndexing siteIndexing;
  private static final Log log = LogFactory.getLog(LinkPull.class);


 public  static  final HashSet<String>allLinks = new HashSet<>();
 // public final static List<String> allLinks = new Vector<>();
 //public final static List<String> allLinks = new CopyOnWriteArrayList<>();
//public  static Collections.synchronizedList(List) allLinks =new list<>();



  public LinkPull(String url, String startUrl, boolean isInterrupted, SiteIndexing siteIndexing) {
    this.url = url.trim();
    this.startUrl = startUrl.trim();
    LinkPull.isInterrupted = isInterrupted;
    this.siteIndexing = siteIndexing;
  }



  @Override
  protected String compute() {
    if (isInterrupted) {
//      log.info("LinkPull.compute return immediately");
      return "";

    }
    StringBuilder sb = new StringBuilder(url + "\n");
    Set<LinkPull> subTask = new HashSet<>();


    try {
      Thread.sleep(200);
      Document doc = receiveDoc();
      if (doc != null) {
        findChildrenAndRunSubTasks(doc, subTask);
        // calculateRank(doc);
        for (LinkPull link : subTask) {
          sb.append(link.join());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      return  "";
    }


    return sb.toString();
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
      System.out.println(url);
      e.printStackTrace();
    }
    return null;
  }


  private void findChildrenAndRunSubTasks(Document doc, Set<LinkPull> subTask) {
//    siteIndexing.setIndexing();
    Elements elements;

    content = doc.html();
    content = content.replaceAll("'", "&#39;");
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
        LinkPull linkExecutor = new LinkPull(attr, startUrl, isInterrupted, siteIndexing);
        linkExecutor.fork();
        subTask.add(linkExecutor);
      }
    }
  }
  public int rand(){
    return (int)Math.random()*5000+500;
  }
}

