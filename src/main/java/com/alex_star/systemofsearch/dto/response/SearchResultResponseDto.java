package com.alex_star.systemofsearch.dto.response;

import com.alex_star.systemofsearch.dto.RelevanceStorageDto;

import java.util.ArrayList;


public class SearchResultResponseDto implements ResultResponseDto {

    private boolean result;
    private int count;
    private ArrayList<RelevanceStorageDto> data;

    public SearchResultResponseDto() {
    }

    public SearchResultResponseDto(boolean result) {
        this.result = result;
    }

    public SearchResultResponseDto(boolean result, int count, ArrayList<RelevanceStorageDto> data) {
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


