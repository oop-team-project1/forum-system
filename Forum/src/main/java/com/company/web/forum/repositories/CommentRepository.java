package com.company.web.forum.repositories;

import com.company.web.forum.helpers.FilterOptionsComments;
import com.company.web.forum.models.Comment;

import java.util.List;

public interface CommentRepository {

    List<Comment> getAll(FilterOptionsComments filterOptions);

    Comment getById(int id);

    void create(Comment comment);

    void update(Comment comment);
}
