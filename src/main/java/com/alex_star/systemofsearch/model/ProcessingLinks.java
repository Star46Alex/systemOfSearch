package com.alex_star.systemofsearch.model;

import javax.persistence.*;

@Entity
@Table(name = "processing_links")
public class ProcessingLinks {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;
  @Column(name = "site_url")
  private String siteUrl;
  @Column(unique=true)
  private String url;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }



  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
  public String getSiteUrl() {
    return siteUrl;
  }

  public void setSiteUrl(String siteUrl) {
    this.siteUrl = siteUrl;
  }


}
