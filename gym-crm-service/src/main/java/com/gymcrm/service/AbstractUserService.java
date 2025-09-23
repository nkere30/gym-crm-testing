package com.gymcrm.service;

import com.gymcrm.dao.AbstractUserJpaDao;
import com.gymcrm.model.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Slf4j
public abstract class AbstractUserService<T extends User> implements UserService<T> {

    protected final AbstractUserJpaDao<T> userDao;
    protected final PasswordEncoder passwordEncoder;

    protected AbstractUserService(AbstractUserJpaDao<T> userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public T create(T user) {
        log.info("Creating {}: {}", user.getClass().getSimpleName(), user);
        return userDao.save(user);
    }

    @Override
    public T update(T user) {
        log.info("Updating {} with ID {}: {}", user.getClass().getSimpleName(), user.getId(), user);
        return userDao.save(user);
    }

    @Override
    public T findById(Long id) {
        log.info("Looking up user by ID: {}", id);
        return userDao.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", id);
                    return new EntityNotFoundException("User not found with ID: " + id);
                });
    }

    @Override
    public List<T> findAll() {
        log.info("Fetching all users");
        return userDao.findAll();
    }

    @Override
    public T findByUsername(String username) {
        log.info("Looking up user by username: {}", username);
        return userDao.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found with username: {}", username);
                    return new EntityNotFoundException("User not found with username: " + username);
                });
    }

    @Override
    public T updatePassword(String username, String newPassword) {
        log.info("Updating password for user: {}", username);
        T user = findByUsername(username);
        user.setPassword(passwordEncoder.encode(newPassword));
        return userDao.save(user);
    }

    @Override
    public void setActiveStatus(String username, boolean active) {
        log.info("Setting active status for user '{}' to: {}", username, active);
        T user = findByUsername(username);
        user.setIsActive(active);
        userDao.save(user);
    }

}
