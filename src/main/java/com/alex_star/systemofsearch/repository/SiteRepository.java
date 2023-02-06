package com.alex_star.systemofsearch.repository;

import com.alex_star.systemofsearch.model.Site;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteRepository extends CrudRepository<Site, Integer> {

  Site findSiteByUrl(String url);
}
