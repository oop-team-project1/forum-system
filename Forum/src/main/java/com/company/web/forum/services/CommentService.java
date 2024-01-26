package com.company.web.forum.services;

import com.company.web.forum.helpers.FilterOptionsPosts;
import com.company.web.forum.models.Comment;
import com.company.web.forum.models.User;

import java.util.List;

public interface CommentService {
    List<Comment> getAll();
    Comment getById(int id);
    void create (Comment comment, User user);
    void update(Comment comment);
}
