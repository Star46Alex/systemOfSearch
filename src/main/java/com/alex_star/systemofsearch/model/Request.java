package com.alex_star.systemofsearch.model;

import com.alex_star.systemofsearch.service.LemmatizerService;

import java.util.ArrayList;
import java.util.List;


public class Request {

    private final String request;
    private final List<String> requestLemmas;

    public List<String> getRequestLemmas() {
        return requestLemmas;
    }

    public String getRequest() {
        return request;
    }

    public Request(List<String> lemmas, String request) {
        this.request = request;
        requestLemmas = lemmas;
    }
}
