package com.company.web.forum.controllers;


import com.company.web.forum.exceptions.EntityNotFoundException;

import com.company.web.forum.models.Comment;
import com.company.web.forum.services.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("tastytale/api/v1/comments")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService service) {
        this.commentService = service;
    }

    @GetMapping("{id}")
    @Operation(
            tags = {"Comment API"},
            summary = "Get a comment by ID",
            description = "Retrieves a comment based on the provided ID.",
            parameters = {@Parameter(name = "id", description = "ID of the comment to retrieve")},
            responses = {@ApiResponse(responseCode = "200",
                    content = @Content(schema =
                    @Schema(implementation = Comment.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    public Comment getById(@PathVariable int id) {
        try {
            return commentService.getById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("{id}/replies")
    @Operation(
            tags = {"Comment API", "Comment Replies API"},
            description = "Get replies for comment",
            parameters = {@Parameter(name = "id", description = "ID of the comment")},
            responses = {@ApiResponse(responseCode = "200",
                    content = @Content(schema =
                    @Schema(implementation = Comment.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}

    )
    public List<Comment> geReplies(@PathVariable int id) {
        try {
            return commentService.getReplies(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
