package com.gymcrm.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class AbstractJpaDao<T, ID> implements CrudDao<T, ID> {

    protected final Class<T> entityClass;

    @PersistenceContext
    protected EntityManager entityManager;

    protected AbstractJpaDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T save(T entity) {
        log.debug("Merging entity of type {}: {}", entityClass.getSimpleName(), entity);
        return entityManager.merge(entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        log.debug("Finding entity of type {} by ID: {}", entityClass.getSimpleName(), id);
        return Optional.ofNullable(entityManager.find(entityClass, id));
    }

    @Override
    public List<T> findAll() {
        String query = "SELECT e FROM " + entityClass.getSimpleName() + " e";
        log.trace("Executing findAll query for {}: {}", entityClass.getSimpleName(), query);
        return entityManager.createQuery(query, entityClass).getResultList();
    }

    @Override
    public void deleteById(ID id) {
        log.debug("Attempting to delete entity of type {} with ID: {}", entityClass.getSimpleName(), id);
        Optional.ofNullable(entityManager.find(entityClass, id))
                .ifPresentOrElse(
                        entityManager::remove,
                        () -> log.warn("Entity not found for deletion. ID: {}", id)
                );
    }
}
