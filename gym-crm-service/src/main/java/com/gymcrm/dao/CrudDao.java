package com.gymcrm.dao;

import java.util.List;
import java.util.Optional;

public interface CrudDao <T, ID>{
    T save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    void deleteById(ID id);
 }
