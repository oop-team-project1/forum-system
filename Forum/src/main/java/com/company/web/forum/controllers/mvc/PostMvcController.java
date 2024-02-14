package com.company.web.forum.controllers.mvc;

import com.company.web.forum.helpers.AuthenticationHelper;
import com.company.web.forum.helpers.FilterOptionsPosts;
import com.company.web.forum.helpers.PostMapper;
import com.company.web.forum.services.CommentService;
import com.company.web.forum.services.PostService;
import com.company.web.forum.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/posts")
public class PostMvcController {
 private final PostService postService;
 private final CommentService commentService;
 private final UserService userService;
 private final AuthenticationHelper authenticationHelper;
 private final PostMapper postMapper;

    public PostMvcController(PostService postService, CommentService commentService, UserService userService, AuthenticationHelper authenticationHelper, PostMapper postMapper) {
        this.postService = postService;
        this.commentService = commentService;
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.postMapper = postMapper;
    }


}

