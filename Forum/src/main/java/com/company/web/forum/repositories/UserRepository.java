package com.company.web.forum.repositories;

import com.company.web.forum.helpers.FilterOptionsUsers;
import com.company.web.forum.models.User;


import java.util.List;


public interface UserRepository {
    List<User> getAll(FilterOptionsUsers filterOptionsUsers);

    User getById(int id);

    User getByUsername(String username);

    User getByEmail(String email);

    void create(User user);

    void update(User user);

    void deleteUser(int id);
}
