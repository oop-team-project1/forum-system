package com.company.web.forum.controllers;

import com.company.web.forum.exceptions.AuthenticationException;
import com.company.web.forum.exceptions.AuthorizationException;
import com.company.web.forum.exceptions.EntityNotFoundException;
import com.company.web.forum.helpers.AuthenticationHelper;
import com.company.web.forum.helpers.CommentMapper;
import com.company.web.forum.helpers.FilterOptionsComments;
import com.company.web.forum.models.Comment;
import com.company.web.forum.models.CommentDto;
import com.company.web.forum.models.Post;
import com.company.web.forum.models.User;
import com.company.web.forum.services.CommentService;
import com.company.web.forum.services.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
//TODO: fix route for all methods
//@RequestMapping("tastytale/api/v1/posts/{postId}/comments")
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
    public List<Comment> getAll(
            //@PathVariable int postId,
            @RequestParam (required= false) String content,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer postId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam (required = false) LocalDate endDate
    ){
        FilterOptionsComments filterOptions = new FilterOptionsComments(content, userId, postId, startDate, endDate);
        return commentService.getAll(filterOptions);
    }
    //TODO: fix route
    @GetMapping("/posts")
    public Comment getById(@RequestParam int id){
        try {
            return commentService.getById(id);
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    public Comment create(@PathVariable int postId,@RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString,
                           @Valid @RequestBody CommentDto commentDto){
           try {
               User user = authenticationHelper.tryGetUser(encodedString);
               Post post = postService.get(postId);
               Comment comment = commentMapper.fromDto(commentDto);
               commentService.create(comment, user, post);
               return comment;
           } catch (AuthorizationException | AuthenticationException e){
               throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
           }
    }

    @PutMapping("/{id}")
    public Comment update (@PathVariable int postId, @PathVariable int id, @RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString,
                           @Valid @RequestBody CommentDto commentDto) {
        try {
            //TODO: add authorization
            User user = authenticationHelper.tryGetUser(encodedString);
            Post post = postService.get(postId);
            Comment comment = commentMapper.fromDto(id, commentDto);
            commentService.update(comment, user);
            return comment;
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }  catch (AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
