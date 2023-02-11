package com.alex_star.systemofsearch.dto.response;

import com.alex_star.systemofsearch.dto.RelevanceStorageDto;

import java.util.ArrayList;


public class SearchResultResponse implements ResultResponse {

  private boolean result;
  private int count;
  private ArrayList<RelevanceStorageDto> data;

  public SearchResultResponse() {
  }

  public SearchResultResponse(boolean result) {
    this.result = result;
  }

  public SearchResultResponse(boolean result, int count, ArrayList<RelevanceStorageDto> data) {
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

  public ArrayList<RelevanceStorageDto> getData() {
    return data;
  }

  public void setData(ArrayList<RelevanceStorageDto> data) {
    this.data = data;
  }
}


