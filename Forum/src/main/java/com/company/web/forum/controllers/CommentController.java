package com.company.web.forum.controllers;


import com.company.web.forum.exceptions.EntityNotFoundException;

import com.company.web.forum.helpers.FilterOptionsComments;
import com.company.web.forum.models.Comment;
import com.company.web.forum.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("tastytale/api/v1/comments")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService service) {
        this.commentService = service;
    }

    @GetMapping()
    public List<Comment> getAll(@RequestParam(required = false) String content,
                                @RequestParam(required = false) Integer userId,
                                @RequestParam(required = false) String username,
                                @RequestParam(required = false) Integer postId,
                                @RequestParam(required = false) String postTitle,
                                @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate startDate,
                                @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate endDate,
                                @RequestParam(required = false) String sortBy,
                                @RequestParam(required = false) String sortOrder) {
        FilterOptionsComments filterOptions = new FilterOptionsComments(content, userId, username,
                postId, postTitle, startDate, endDate, sortBy, sortOrder);
        return commentService.getAll(filterOptions);
    }

    @GetMapping("{id}")
    public Comment getById(@PathVariable int id) {
        try {
            return commentService.getById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
