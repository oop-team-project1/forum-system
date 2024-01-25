package com.company.web.forum.repositories;

import com.company.web.forum.helpers.FilterOptions;
import com.company.web.forum.models.User;


import java.util.List;


public interface UserRepository {
    List<User> getAll(FilterOptions filterOptions);
    User getById(int id);
    User getByUsername(String username);
    User getByEmail(String email);
    void create(User user);
    void update(User user);
}
