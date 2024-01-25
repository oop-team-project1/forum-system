package com.company.web.forum.services;

import com.company.web.forum.exceptions.AuthorizationException;
import com.company.web.forum.helpers.FilterOptionsPosts;
import com.company.web.forum.models.Comment;
import com.company.web.forum.models.User;
import com.company.web.forum.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService{
    public static final String MODIFY_COMMENT_ERROR_MESSAGE = "Only comment creator can modify this comment!";
    private final CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public List<Comment> getAll(FilterOptionsPosts filterOptionsPosts) {
        return null;
    }

    @Override
    public Comment getById(int id) {
        return commentRepository.getById(id);
    }

    @Override
    public void create(Comment comment, User user) {
        comment.setCreatedBy(user);
        commentRepository.create(comment);
    }

    @Override
    public void update(Comment comment) {
        //check permissions
        //TODO: check if user is blocked
        commentRepository.update(comment);
    }

    private void checkModifyPermissions(int commentId, User user) {
        Comment comment = commentRepository.getById(commentId);
        if(!comment.getCreatedBy().equals(user)){
            throw new AuthorizationException(MODIFY_COMMENT_ERROR_MESSAGE);
        }
    }
}
