package com.alex_star.systemofsearch.model;

import com.alex_star.systemofsearch.lemmatizer.Lemmatizer;

import java.util.ArrayList;
import java.util.List;


public class Request {

  private String request;
  private List<String> requestLemmas;

  public List<String> getRequestLemmas() {
    return requestLemmas;
  }

  public String getRequest() {
    return request;
  }

  public Request(String request) {
    this.request = request;
    requestLemmas = new ArrayList<>();
    try {
      Lemmatizer lemmatizer = new Lemmatizer();
      requestLemmas.addAll(lemmatizer.getLemmas(request));
    } catch (Exception e) {
      System.out.println("ошибка морфологочиского анализа");
    }
  }
}
