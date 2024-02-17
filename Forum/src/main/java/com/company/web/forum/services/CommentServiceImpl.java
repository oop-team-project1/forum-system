package com.company.web.forum.services;

import com.company.web.forum.exceptions.AuthorizationException;
import com.company.web.forum.exceptions.EntityNotFoundException;
import com.company.web.forum.helpers.FilterOptionsComments;
import com.company.web.forum.models.Comment;
import com.company.web.forum.models.Post;
import com.company.web.forum.models.User;
import com.company.web.forum.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    public static final String PERMISSION_FOR_MODIFYING_COMMENTS_ERROR_MESSAGE = "You are not the owner of the comment! You don't have permission to modify it!";
    public static final String BLOCKED_USER_MESSAGE = "Unable to create comment, user is blocked";
    private final CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public List<Comment> getAll(FilterOptionsComments filterOptions) {
        return commentRepository.getAll(filterOptions);
    }

    @Override
    public Comment getById(int id) {
        return commentRepository.getById(id);
    }

    @Override
    public List<Comment> getReplies(int id) {
        return commentRepository.getReplies(id);
    }

    @Override
    public void create(Comment comment, User user, Post post, Comment parentComment) {
        checkIfUserIsBlocked(user);
        comment.setCreatedBy(user);
        comment.setPost(post);
        comment.setParentComment(parentComment);
        commentRepository.create(comment);
    }

    @Override
    public void update(Comment comment, User user) {
        checkIfUserIsBlocked(user);
        checkIfUserIsOwnerOfTheComment(comment, user);
        commentRepository.update(comment);
    }

    @Override
    public void deleteComment(int id, User user) {
        Comment comment = commentRepository.getById(id);
        checkIfUserIsBlocked(user);
        checkIfUserIsOwnerOfTheComment(comment,user);
        commentRepository.deleteComment(id);
    }

    private void checkIfUserIsBlocked(User user) {
        if (user.isBlocked()) {
            throw new AuthorizationException(BLOCKED_USER_MESSAGE);
        }
    }

    private void checkIfUserIsOwnerOfTheComment(Comment comment, User user) {
        if (user.getId() != comment.getCreatedBy().getId()) {
            throw new AuthorizationException(PERMISSION_FOR_MODIFYING_COMMENTS_ERROR_MESSAGE);
        }
    }
}
