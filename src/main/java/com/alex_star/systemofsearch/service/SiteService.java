package com.alex_star.systemofsearch.service;

import com.alex_star.systemofsearch.model.Site;
import com.alex_star.systemofsearch.repository.SiteRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SiteService {

    private final SiteRepository siteRepository;

    public SiteService(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }


    public Site getSite(String url) {
        return siteRepository.findSiteByUrl(url);
    }

    public Site getSite(int siteId) {
        Optional<Site> optional = siteRepository.findById(siteId);
        Site site = null;
        if (optional.isPresent()) {
            site = optional.get();
        }
        return site;
    }


    public synchronized void save(Site site) {
        siteRepository.save(site);
    }


    public long siteCount() {
        return siteRepository.count();
    }


    public List<Site> getAllSites() {
        List<Site> siteList = new ArrayList<>();
        Iterable<Site> it = siteRepository.findAll();
        it.forEach(siteList::add);
        return siteList;
    }
}


