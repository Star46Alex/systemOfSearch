package com.alex_star.systemofsearch.repository;

import com.alex_star.systemofsearch.model.Indexing;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndexingRepository extends CrudRepository<Indexing, Integer> {

    List<Indexing> findByLemmaId(int lemmaId);


}

