package com.alex_star.systemofsearch.repository;

import com.alex_star.systemofsearch.model.ProcessingLinks;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessingLinksRepository extends CrudRepository<ProcessingLinks, Integer> {
 List<ProcessingLinks> findProcessingLinksBySiteUrl(String siteUrl);
}
