package com.gymcrm.service;

import com.gymcrm.model.User;

import java.util.List;

public interface UserService<T extends User>{

    T create(T user);

    T update(T user);

    T findById(Long id);

    List<T> findAll();

    T findByUsername(String username);

    T updatePassword(String username, String newPassword);

    void setActiveStatus(String username, boolean active);
}
