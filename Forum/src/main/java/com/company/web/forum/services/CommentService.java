package com.company.web.forum.services;

import com.company.web.forum.helpers.FilterOptionsComments;
import com.company.web.forum.models.Comment;
import com.company.web.forum.models.Post;
import com.company.web.forum.models.User;

import java.util.List;

public interface CommentService {
    List<Comment> getAll(FilterOptionsComments filterOptions);

    Comment getById(int id);

    List<Comment> getReplies(int id);

    void create(Comment comment, User user, Post post, Comment parentComment);

    void update(Comment comment, User user);

    void deleteComment(int id, User user);
}
