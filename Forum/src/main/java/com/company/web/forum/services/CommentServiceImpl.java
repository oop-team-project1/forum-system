package com.company.web.forum.services;

import com.company.web.forum.helpers.FilterOptions;
import com.company.web.forum.models.Comment;
import com.company.web.forum.repositories.CommentRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService{
    private final CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public List<Comment> getAll(FilterOptions filterOptions) {
        return null;
    }

    @Override
    public Comment getById(int id) {
        return commentRepository.getById(id);
    }

    @Override
    public void create(Comment comment) {

    }

    @Override
    public void update(Comment comment) {

    }
}
