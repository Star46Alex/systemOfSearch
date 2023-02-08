package com.alex_star.systemofsearch.repository;

import com.alex_star.systemofsearch.model.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PageRepository extends CrudRepository<Page, Integer> {


  Page findByPath(String path);

  Optional<Page> findPageById(int id);

  @Query(value = "SELECT count(*) from Page where site_id = :id", nativeQuery = true)
  long count(@Param("id") long id);
}

