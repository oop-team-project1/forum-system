package com.company.web.forum.repositories;

import com.company.web.forum.helpers.FilterOptionsComments;
import com.company.web.forum.models.Comment;

import java.util.List;

public interface CommentRepository {


    Comment getById(int id);

    List<Comment> getReplies(int id);

    void create(Comment comment);

    void update(Comment comment);

    void deleteComment(int id);
}
