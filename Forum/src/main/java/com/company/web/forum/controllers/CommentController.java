package com.company.web.forum.controllers;

import com.company.web.forum.exceptions.AuthorizationException;
import com.company.web.forum.exceptions.EntityNotFoundException;
import com.company.web.forum.helpers.AuthenticationHelper;
import com.company.web.forum.helpers.CommentMapper;
import com.company.web.forum.models.Comment;
import com.company.web.forum.models.CommentDto;
import com.company.web.forum.models.User;
import com.company.web.forum.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("tastytale/api/v1/comments")
public class CommentController {
    public static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private final CommentService service;
    private final AuthenticationHelper authenticationHelper;
    private final CommentMapper commentMapper;

    @Autowired
    public CommentController(CommentService service, AuthenticationHelper authenticationHelper, CommentMapper commentMapper) {
        this.service = service;
        this.authenticationHelper = authenticationHelper;
        this.commentMapper = commentMapper;
    }
    @GetMapping()
    public Comment getById(@RequestParam int id){
        try {
            return service.getById(id);
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping
    public Comment update (@RequestParam int post_id, @Valid @RequestBody CommentDto commentDto) {
        try {
            //TODO: add mapper, authorization
           // String encodedString = headers.getFirst(AUTHORIZATION_HEADER_NAME);
            //User user = authenticationHelper.tryGetUser(encodedString);
            Comment comment = commentMapper.fromDto(post_id, commentDto);
            service.update(comment);
            return comment;
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }  catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
