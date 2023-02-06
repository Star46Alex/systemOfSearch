package com.alex_star.systemofsearch.dto.searchResponseStorage;

public class RelevanceStorage {

  String site;
  String siteName;
  String uri;
  String title;
  String snippet;
  double relevance;


  public RelevanceStorage(String uri, String title) {
    this.uri = uri;
    this.title = title;
  }
  public RelevanceStorage(String site, String siteName, String uri, String title, String snippet, double relevance) {
    this.site = site;
    this.siteName = siteName;
    this.uri = uri;
    this.title = title;
    this.snippet = snippet;
    this.relevance = relevance;
  }

  public RelevanceStorage() {

  }

//  public RelevanceStorage() {
//
//  }


  public String getSite() {
    if (site != null) {
      return site;
    }
    return "";
  }

  public void setSite(String site) {
    this.site = site;
  }

  public String getSiteName() {
    return siteName;
  }

  public void setSiteName(String siteName) {
    this.siteName = siteName;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSnippet() {
    return snippet;
  }

  public void setSnippet(String snippet) {
    this.snippet = snippet;
  }

  public double getRelevance() {
    return relevance;
  }

  public void setRelevance(double relevance) {
    this.relevance = relevance;
  }

  @Override
  public String toString() {
    return "SearchData{" +
        "uri='" + uri + '\'' +
        ", title='" + title + '\'' +
        ", snippet='" + snippet + '\'' +
        ", relevance=" + relevance +
        '}';
  }
}



