package com.company.web.forum.controllers;

import com.company.web.forum.exceptions.AuthorizationException;
import com.company.web.forum.exceptions.EntityNotFoundException;
import com.company.web.forum.helpers.AuthenticationHelper;
import com.company.web.forum.helpers.CommentMapper;
import com.company.web.forum.models.Comment;
import com.company.web.forum.models.CommentDto;
import com.company.web.forum.models.Post;
import com.company.web.forum.services.CommentService;
import com.company.web.forum.services.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("tastytale/api/v1/comments")
public class CommentController {
    public static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private final CommentService commentService;
    private final PostService postService;
    private final AuthenticationHelper authenticationHelper;
    private final CommentMapper commentMapper;

    @Autowired
    public CommentController(CommentService service,
                             AuthenticationHelper authenticationHelper,
                             CommentMapper commentMapper,
                             PostService postService) {
        this.commentService = service;
        this.authenticationHelper = authenticationHelper;
        this.commentMapper = commentMapper;
        this.postService = postService;
    }
    @GetMapping()
    public List<Comment> getAll(){
        return commentService.getAll();
    }
    @GetMapping("/posts")
    public Comment getById(@RequestParam int id){
        try {
            return commentService.getById(id);
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Comment update (@PathVariable int id, @Valid @RequestBody CommentDto commentDto) {
        try {
            //TODO: add authorization
            Comment comment = commentMapper.fromDto(id, commentDto);
            commentService.update(comment);
            return comment;
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }  catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
