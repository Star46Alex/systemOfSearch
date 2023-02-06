package com.alex_star.systemofsearch.dto.response;

import com.alex_star.systemofsearch.dto.searchResponseStorage.RelevanceStorage;

import java.util.ArrayList;


public class SearchResponse implements ResponseService {

  private boolean result;
  private int count;
  private ArrayList<RelevanceStorage> data;

  public SearchResponse() {
  }

  public SearchResponse(boolean result) {
    this.result = result;
  }

  public SearchResponse(boolean result, int count, ArrayList<RelevanceStorage> data) {
    this.result = result;
    this.count = count;
    this.data = data;
  }
  @Override
  public boolean getResult() {
    return result;
  }

  public void setResult(boolean result) {
    this.result = result;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public ArrayList<RelevanceStorage> getData() {
    return data;
  }

  public void setData(ArrayList<RelevanceStorage> data) {
    this.data = data;
  }
}


