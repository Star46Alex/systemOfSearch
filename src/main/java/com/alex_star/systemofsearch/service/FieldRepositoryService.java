package com.alex_star.systemofsearch.service;

import com.alex_star.systemofsearch.model.Field;
import com.alex_star.systemofsearch.repository.FieldRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FieldRepositoryService {

  private final FieldRepository fieldRepository;

  public FieldRepositoryService(FieldRepository fieldRepository) {
    this.fieldRepository = fieldRepository;
  }


  public Field getFieldByName(String fieldName) {
    return fieldRepository.findByName(fieldName);
  }


  public synchronized void save(Field field) {
    fieldRepository.save(field);
  }

  public List<Field> getAllField() {
    List<Field> list = new ArrayList<>();
    Iterable<Field> iterable = fieldRepository.findAll();
    iterable.forEach(list::add);
    return list;
  }
}

