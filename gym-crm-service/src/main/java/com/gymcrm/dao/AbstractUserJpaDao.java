package com.gymcrm.dao;

import com.gymcrm.model.User;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public abstract class AbstractUserJpaDao<T extends User> extends AbstractJpaDao<T, Long> {

    protected AbstractUserJpaDao(Class<T> entityClass) {
        super(entityClass);
    }

    public Optional<T> findByUsername(String username) {
        String query = "SELECT u FROM " + entityClass.getSimpleName() + " u WHERE u.username = :username";
        log.debug("Executing findByUsername for {}: {}, JPQL: {}", entityClass.getSimpleName(), username, query);
        return entityManager
                .createQuery(query, entityClass)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }
}
