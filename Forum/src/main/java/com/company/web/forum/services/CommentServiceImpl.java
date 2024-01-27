package com.company.web.forum.services;

import com.company.web.forum.exceptions.AuthorizationException;
import com.company.web.forum.helpers.FilterOptionsPosts;
import com.company.web.forum.models.Comment;
import com.company.web.forum.models.Post;
import com.company.web.forum.models.User;
import com.company.web.forum.repositories.CommentRepository;
import com.company.web.forum.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService{
    public static final String MODIFY_COMMENT_ERROR_MESSAGE = "Only comment creator can modify this comment!";
    public static final String BLOCKED_USERS_EXCEPTION_MESSAGE = "Blocked users can't create or modify comments!";
    public static final String PERMISSION_FOR_MODIFYING_COMMENTS_ERROR_MESSAGE = "You are not the owner of the comment! You don't have permission to modify it!";
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @Override
    public List<Comment> getAll() {
        List<Comment> comments = commentRepository.getAll();
        return comments;
    }

    @Override
    public Comment getById(int id) {
        return commentRepository.getById(id);
    }

    @Override
    public void create(Comment comment, User user, Post post) {
        //TODO: check if user is logged
        checkIfUserIsBlocked(user);
        comment.setCreatedBy(user);
        comment.setPost(post);
        commentRepository.create(comment);
    }

    @Override
    public void update(Comment comment, User user) {
        //TODO: check if user is logged
        checkIfUserIsBlocked(user);
        checkIfUserIsOwnerOfTheComment(comment, user);
        commentRepository.update(comment);
    }

    private void checkIfUserIsBlocked(User user){
        if (user.isBlocked()){
            throw new AuthorizationException(BLOCKED_USERS_EXCEPTION_MESSAGE);
        }
    }
    private void checkIfUserIsOwnerOfTheComment (Comment comment, User user) {
            if(user.getId() != comment.getCreatedBy().getId()) {
                throw new AuthorizationException(PERMISSION_FOR_MODIFYING_COMMENTS_ERROR_MESSAGE);
            }
    }
}
