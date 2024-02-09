package com.company.web.forum.services;

import com.company.web.forum.helpers.FilterOptionsUsers;
import com.company.web.forum.models.User;

import java.util.List;

public interface UserService {
    List<User> getAll(FilterOptionsUsers filterOptionsUsers);

    User getById(int id);

    User getByUsername(String username);

    User getByEmail(String email);

    void addPost(int userId, int postId, User user);

    void removePost(int userId, int postId, User user);

    void blockUser(int id, User user);

    void unblockUser(int id, User user);

    void create(User userToCreate);

    void update(User userToUpdate, User user);

    void makeAdmin(int id, User user);

    void removeAdmin(int id, User user);
}
