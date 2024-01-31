package com.company.web.forum.services;

import com.company.web.forum.helpers.FilterOptionsUsers;
import com.company.web.forum.models.User;

import java.util.List;

public interface UserService
{
    List<User> getAll(FilterOptionsUsers filterOptionsUsers, User user);
    User getById(int id, User user);
    User getByUsername(String username);
    User getByEmail(String email, User user);

    void addPost(int userId, int postId, User user);

    void removePost(int userId, int postId, User user);

    void blockUser(String username, User user);

    void unblockUser(String username, User user);

    void create(User userToCreate);

    void update(User userToUpdate, User user);
}
