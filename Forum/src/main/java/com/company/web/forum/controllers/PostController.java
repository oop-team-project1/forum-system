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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
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
    public PostController(PostService postService, AuthenticationHelper authenticationHelper, PostMapper postMapper, CommentMapper commentMapper, CommentService commentService) {
        this.authenticationHelper = authenticationHelper;
        this.postService = postService;
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
        this.commentService = commentService;
    }

    @GetMapping
    @Operation(
            tags = {"Post API"},
            summary = "Get posts with filters",
            description = "Retrieves a list of posts based on specified filter options.",
            parameters = {@Parameter(name = "Authorization", example = "Authorization"),
                    @Parameter(name = "author", description = "Author username"),
                    @Parameter(name = "title", description = "Title of the content between 16 and 64 symbols."),
                    @Parameter(name = "content", description = "Content body between 32 and 8192 symbols."),
                    @Parameter(name = "dateFrom", description = "Start date."),
                    @Parameter(name = "dateUntil", description = "End date."),
                    @Parameter(name = "orderBy", description = "Order by likes/date posted or comments."),
                    @Parameter(name = "order", description = "desc")
            },
            responses = {@ApiResponse(responseCode = "200",
                    content = @Content(schema =
                    @Schema(implementation = Post.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))},
            security = {@SecurityRequirement(name = "basic")}
    )
    public List<Post> get(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString,
                          @RequestParam(required = false) String author,
                          @RequestParam(required = false) String title,
                          @RequestParam(required = false) String content,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) LocalDate dateFrom,
                          @RequestParam(required = false) LocalDate dateUntil,
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
    @Operation(
            tags = {"Post API"},
            summary = "Get a post by ID",
            description = "Retrieves a post based on the provided ID.",
            parameters = {@Parameter(name = "Authorization", example = "Authorization"),
                    @Parameter(name = "id", description = "ID of the post to retrieve")},
            responses = {@ApiResponse(responseCode = "200",
                    content = @Content(schema =
                    @Schema(implementation = Post.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))},
            security = {@SecurityRequirement(name = "basic")}
    )
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

    @PostMapping
    @Operation(
            tags = {"Post API"},
            summary = "Create a new post",
            description = "Creates a new post.",
            parameters = {@Parameter(name = "Authorization", example = "Authorization")},
            requestBody =
            @io.swagger.v3.oas.annotations.parameters.RequestBody
                    (description = "Title must be between 16 and 64 symbols and" +
                            " content must be between 32 and 8192 symbols."),
            security = {@SecurityRequirement(name = "basic")},
            responses = {@ApiResponse(responseCode = "200",
                    content = @Content(schema =
                    @Schema(implementation = Post.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))}
    )
    public Post create(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString,
                       @Valid @RequestBody PostDto postDto) {
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
    @Operation(
            tags = {"Post API"},
            summary = "Update a post",
            description = "Updates an existing post based on the provided ID.",
            parameters = {@Parameter(name = "Authorization", example = "Authorization"),
                    @Parameter(name = "id", description = "ID of the post")},
            security = {@SecurityRequirement(name = "basic")})
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
    @Operation(
            tags = {"Post API"},
            summary = "Delete a post",
            description = "Deletes a post based on the provided ID.",
            parameters = {@Parameter(name = "Authorization", example = "Authorization"),
                    @Parameter(name = "id", description = "ID of the post")},
            security = {@SecurityRequirement(name = "basic")}
    )
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


    @DeleteMapping("/selection")
    @Operation(
            tags = {"Post API"},
            summary = "Delete multiple posts",
            description = "Deletes multiple posts based on the provided list of record IDs.",
            requestBody =
            @io.swagger.v3.oas.annotations.parameters.RequestBody
                    (description = "ID's of the posts you want to delete, separated by comma."),
            parameters = {@Parameter(name = "Authorization", example = "Authorization")},
            security = {@SecurityRequirement(name = "basic")}
    )
    public void deleteMultiple(@RequestBody List<Integer> records, @RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(encodedString);
            postService.deleteMultiple(records, user);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }


    }

    @PostMapping("/{id}/comments")
    @Operation(
            tags = {"Comment API"},
            summary = "Create a new comment",
            description = "Creates a new comment for a specific post.",
            parameters = {@Parameter(name = "Authorization", example = "Authorization"),
                    @Parameter(name = "id", description = "The ID of the post for which the comment is being added.")},
            responses = {@ApiResponse(responseCode = "200",
                    content = @Content(schema =
                    @Schema(implementation = Comment.class),
                            mediaType = MediaType.APPLICATION_JSON_VALUE))},
            security = {@SecurityRequirement(name = "basic")}
    )
    public Comment create(@PathVariable int id,
                          @RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString,
                          @Valid @RequestBody CommentDto commentDto) {
        try {
            User user = authenticationHelper.tryGetUser(encodedString);
            Post post = postService.get(id);
            Comment comment = commentMapper.fromDto(commentDto);
            commentService.create(comment, user, post, comment);
            return comment;
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{id}/comments/{commentId}/replies")
    @Operation(
            tags = {"Comment API", "Comment Replies API"},
            summary = "Create a reply to a comment",
            description = "Creates a reply to a specific comment.",
            parameters = {@Parameter(name = "Authorization", example = "Authorization"),
                    @Parameter(name = "id", description = "Post ID"),
                    @Parameter(name = "commentId", description = "Comment ID")},
            security = {@SecurityRequirement(name = "basic")}
    )
    public Comment createReply(@PathVariable int id,
                               @RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString,
                               @PathVariable int commentId,
                               @Valid @RequestBody CommentDto commentDto) {
        try {
            User user = authenticationHelper.tryGetUser(encodedString);
            Post post = postService.get(id);
            Comment parentComment = commentService.getById(commentId);
            Comment reply = commentMapper.fromDto(commentDto);
            commentService.create(reply, user, post, parentComment);
            return reply;
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{id}/comments/{commentId}")
    @Operation(
            tags = {"Comment API"},
            summary = "Update a comment",
            description = "Updates an existing comment.",
            parameters = {@Parameter(name = "id", description = "ID of the post containing the comment."),
                    @Parameter(name = "Authorization", example = "Authorization"),
                    @Parameter(name = "commentId", description = "ID of the comment to update"),},
            security = {@SecurityRequirement(name = "basic")}
    )
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

    @PostMapping("/{id}/likes")
    public Post likePostByUser(@PathVariable int id, @RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString) {
        try {
            User user = authenticationHelper.tryGetUser(encodedString);
            return postService.addUserToLikes(user, id);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    @DeleteMapping("/{id}/likes")
    public Post dislikePostByUser(@PathVariable int id, @RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString) {
        try {
            User user = authenticationHelper.tryGetUser(encodedString);
            return postService.removeUserFromLikes(user, id);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }
}
