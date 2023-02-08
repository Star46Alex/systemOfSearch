package com.alex_star.systemofsearch.service;

import com.alex_star.systemofsearch.model.Page;
import com.alex_star.systemofsearch.repository.PageRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class PageRepositoryService {

  private final PageRepository pageRepository;

  public PageRepositoryService(PageRepository pageRepository) {
    this.pageRepository = pageRepository;


  }

  public Page getPage(String path) {
    return pageRepository.findByPath(path);
  }



  @Cacheable("pages")
  public Optional<Page> findPageById(int id) {
    return pageRepository.findPageById(id);
  }


  public synchronized void save(Page page) {
    pageRepository.save(page);
  }


  public long pageCount() {
    return pageRepository.count();
  }


  public long pageCount(long siteId) {
    return pageRepository.count(siteId);
  }

}

