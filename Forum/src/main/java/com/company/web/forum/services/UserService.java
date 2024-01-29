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

    void addPost(int userId, int postId);

    void removePost(int userId, int postId);

    void blockUser(int userId);

    void unblockUser(int userId);

    void create(User userToCreate);

    void update(User userToUpdate);
}
