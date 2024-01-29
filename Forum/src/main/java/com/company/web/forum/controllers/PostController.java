package com.company.web.forum.controllers;

import com.company.web.forum.exceptions.AuthenticationException;
import com.company.web.forum.exceptions.AuthorizationException;
import com.company.web.forum.exceptions.EntityNotFoundException;
import com.company.web.forum.helpers.AuthenticationHelper;
import com.company.web.forum.helpers.CommentMapper;
import com.company.web.forum.helpers.FilterOptionsPosts;
import com.company.web.forum.helpers.PostMapper;
import com.company.web.forum.models.*;
import com.company.web.forum.services.CommentService;
import com.company.web.forum.services.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("tastytale/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final AuthenticationHelper authenticationHelper;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final CommentService commentService;

    @Autowired
    public PostController(PostService postService, AuthenticationHelper authenticationHelper, PostMapper postMapper,
                          CommentMapper commentMapper, CommentService commentService) {
        this.authenticationHelper = authenticationHelper;
        this.postService = postService;
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
        this.commentService = commentService;
    }

    @GetMapping
    public List<Post> get(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString,
                          @RequestParam(required = false) String author,
                          @RequestParam(required = false) String title,
                          @RequestParam(required = false) String content,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) LocalDateTime dateFrom,
                          @RequestParam(required = false) LocalDateTime dateUntil,
                          @RequestParam(required = false) List<String> tags,
                          @RequestParam(required = false) List<String> tags_exclude,
                          @RequestParam(required = false) String orderBy,
                          @RequestParam(required = false) String order) {
        FilterOptionsPosts filterOptions = new FilterOptionsPosts(author, title, content, keyword, dateFrom, dateUntil, tags, tags_exclude, orderBy, order);
        try {
            authenticationHelper.tryGetUser(encodedString);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return postService.getAll(filterOptions);
    }


    @GetMapping("/{id}")
    public Post get(@PathVariable int id, @RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString) {
        try {
            authenticationHelper.tryGetUser(encodedString);
            return postService.get(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    public Post create(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString, @Valid @RequestBody PostDto postDto) {
        try {
            User user = authenticationHelper.tryGetUser(encodedString);
            Post post = postMapper.fromDto(postDto);
            postService.create(post, user);

            return post;
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/{id}")
    public Post update(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString,
                       @Valid @RequestBody PostDto postDto,
                       @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(encodedString);
            Post post = postMapper.fromDto(id, postDto);
            postService.update(post, user);
            return post;
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id,
                       @RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString) {
        try {
            User user = authenticationHelper.tryGetUser(encodedString);
            postService.delete(id, user);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

    }


    @PostMapping("/delete-requests")
    public List<Integer> deleteMultiple(@RequestBody List<Integer> records, @RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(encodedString);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        //postService.deleteMultiple(records,user);
        //TODO make it in one query
        for (int record : records) {
            try {
                postService.delete(record, user);
                //TODO Think if something else can be thrown
                // other than Authentication and EntityNotFound
            } catch (RuntimeException e) {
                records.remove(record);
            }
        }
        return records;


    }

    @PostMapping("/{id}/comments")
    public Comment create(@PathVariable int id,
                          @RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString,
                          @Valid @RequestBody CommentDto commentDto) {
        try {
            User user = authenticationHelper.tryGetUser(encodedString);
            Post post = postService.get(id);
            Comment comment = commentMapper.fromDto(commentDto);
            commentService.create(comment, user, post);
            return comment;
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{id}/comments/{commentId}")
    public Comment update(@PathVariable int id, @PathVariable int commentId,
                          @RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString,
                          @Valid @RequestBody CommentDto commentDto) {
        try {
            //TODO: add authorization
            User user = authenticationHelper.tryGetUser(encodedString);
            Comment comment = commentMapper.fromDto(commentId, commentDto);
            commentService.update(comment, user);
            return comment;
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }
}
