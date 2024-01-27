package com.company.web.forum.services;

import com.company.web.forum.helpers.FilterOptionsUsers;
import com.company.web.forum.models.User;

import java.util.List;

public interface UserService
{
    List<User> getAll(FilterOptionsUsers filterOptionsUsers);
    User getById(int id);
    User getByUsername(String username);
    User getByEmail(String email);
}
