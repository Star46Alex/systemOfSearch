package com.alex_star.systemofsearch.repository;

import com.alex_star.systemofsearch.model.Field;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldRepository extends CrudRepository<Field, Integer> {

  Field findByName(String name);
}

