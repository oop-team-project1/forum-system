package com.company.web.forum.controllers.mvc;

import com.company.web.forum.exceptions.AuthorizationException;
import com.company.web.forum.exceptions.EntityNotFoundException;
import com.company.web.forum.helpers.AuthenticationHelper;
import com.company.web.forum.helpers.CommentMapper;
import com.company.web.forum.helpers.FilterOptionsPosts;
import com.company.web.forum.helpers.PostMapper;
import com.company.web.forum.models.Post;
import com.company.web.forum.models.*;
import com.company.web.forum.services.CommentService;
import com.company.web.forum.services.PostService;
import com.company.web.forum.services.TagService;
import com.company.web.forum.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/posts")
public class PostMvcController {
    private final PostService postService;
    private final CommentService commentService;
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final TagService tagService;

    @Autowired
    public PostMvcController(PostService postService, CommentService commentService,
                             UserService userService, AuthenticationHelper authenticationHelper,
                             PostMapper postMapper, CommentMapper commentMapper, TagService tagService) {
        this.postService = postService;
        this.commentService = commentService;
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
        this.tagService = tagService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }
    @GetMapping
    public String showAllPosts(@ModelAttribute("filterOptions") FilterOptionsDto filterDto, Model model, HttpSession session) {
        FilterOptionsPosts filterOptionsPosts = new FilterOptionsPosts(
                filterDto.getContent());
        List<Post> posts = postService.getAll(filterOptionsPosts);
        if (populateIsAuthenticated(session)) {
            String currentUsername = (String) session.getAttribute("currentUser");
            model.addAttribute("currentUser", userService.getByUsername(currentUsername));
        }
        model.addAttribute("filterOptions", filterDto);
        model.addAttribute("posts", posts);
        model.addAttribute("tags", tagService.getTrending(10));
        return "PostsView";
    }


    @GetMapping("/{id}")
    public String showSinglePost(@PathVariable int id, Model model,
                                 HttpSession session) {
        if (populateIsAuthenticated(session)) {
            String currentEmail = (String) session.getAttribute("currentUser");
            model.addAttribute("currentUser", userService.getByEmail(currentEmail));
        }

        try {
            Post post = postService.get(id);
            model.addAttribute("post", post);
            return "PostView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/{id}/comments")
    public String showAddCommentPage(@PathVariable int id, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Post post = postService.get(id);
            model.addAttribute("post", post);
            model.addAttribute("comment", new CommentDto());
            return "CommentView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }


    }

    @PostMapping("/{id}/comments")
    public String createComment(@PathVariable int id,
                                @Valid @ModelAttribute("comment") CommentDto commentDto,
                                BindingResult bindingResult,
                                Model model,
                                HttpSession httpSession) {

        User user;
        try {
            user = authenticationHelper.tryGetUser(httpSession);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) {
            return "redirect:/posts/" + id;
        }

        Post post;
        try {
            post = postService.get(id);
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }

        try {
            Comment comment = commentMapper.fromDto(commentDto);
            commentService.create(comment, user, post, comment);
            return "redirect:/posts/" + id;
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/{id}/update")
    public String showEditPostPage(@PathVariable int id,
                                   Model model,
                                   HttpSession httpSession) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(httpSession);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {

            Post post = postService.get(id);
            PostDto postDto = postMapper.toDto(post);
            model.addAttribute("postId", id);
            model.addAttribute("post", postDto);
            return "PostUpdateView";

        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @PostMapping("/{id}/update")
    public String updatePost(@PathVariable int id,
                             @Valid @ModelAttribute("post") PostDto dto,
                             BindingResult bindingResult,
                             Model model,
                             HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) {
            return "PostUpdateView";
        }

        try {
            Post oldPost = postService.get(id);
            Post post = postMapper.fromDtoUpdating(dto, oldPost);
            postService.update(post, user);
            return "redirect:/posts/" + id;
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/{id}/delete")
    public String deletePost(@PathVariable int id, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            postService.delete(id, user);
            return "redirect:/";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/{id}/comments/{commentId}/replies")
    public String showAddCommentReplyPage(@PathVariable int id,
                                          @PathVariable int commentId,
                                          Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Comment comment = commentService.getById(commentId);
            Post post = postService.get(id);
            model.addAttribute("post", post);
            model.addAttribute("comment", comment);
            model.addAttribute("reply", new CommentDto());
            return "ReplyView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }


    }

    @PostMapping("/{id}/comments/{commentId}/replies")
    public String createComment(@PathVariable int id,
                                @PathVariable int commentId,
                                @Valid @ModelAttribute("comment") CommentDto commentDto,
                                BindingResult bindingResult,
                                Model model,
                                HttpSession httpSession) {

        User user;
        try {
            user = authenticationHelper.tryGetUser(httpSession);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) {
            return "redirect:/posts/" + id;
        }

        Post post;
        Comment parentComment;

        try {
            parentComment = commentService.getById(commentId);
            post = postService.get(id);
            Comment comment = commentMapper.fromDto(commentDto);
            commentService.create(comment, user, post, parentComment);
            return "redirect:/posts/" + id;
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }


    @GetMapping("/filterByTag")
    public String filterPostsByTag(@RequestParam("tag") String tag, Model model) {

        List<String> tags = new ArrayList<>();
        tags.add(tag);
        List<Post> filteredPosts = postService.getAll(new FilterOptionsPosts(tags));

        model.addAttribute("posts", filteredPosts);
        model.addAttribute("tag", tag);

        return "FilteredPostsView";
    }

    @PostMapping("/delete-selection")
    public String deletePosts(@RequestParam("selectedPosts") List<Integer> selectedPostsIds, HttpSession session, Model model) {
        if (populateIsAuthenticated(session)) {
            String currentUsername = (String) session.getAttribute("currentUser");
            model.addAttribute("currentUser", userService.getByUsername(currentUsername));
        }
        postService.deleteMultiple(selectedPostsIds, userService.getByUsername((String) session.getAttribute("currentUser")));
        return "redirect:/posts";
    }
}

