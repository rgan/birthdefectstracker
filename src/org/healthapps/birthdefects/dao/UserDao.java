package org.healthapps.birthdefects.dao;

import org.healthapps.birthdefects.model.User;

import java.util.Collection;

import com.google.storage.onestore.v3.OnestoreEntity;

public interface UserDao {

    void save(User user);

    Collection<User> allUsers();

    User findByName(String name);

    void disableUser(Long id);

    User findUserById(Long id);
}
