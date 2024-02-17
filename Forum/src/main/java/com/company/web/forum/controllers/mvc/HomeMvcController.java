package com.company.web.forum.controllers.mvc;

import com.company.web.forum.helpers.AuthenticationHelper;
import com.company.web.forum.helpers.FilterOptionsPosts;
import com.company.web.forum.models.FilterOptionsDto;
import com.company.web.forum.models.Post;
import com.company.web.forum.models.User;
import com.company.web.forum.services.TagService;
import com.company.web.forum.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import com.company.web.forum.services.PostService;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class HomeMvcController {
    final PostService postService;
    final AuthenticationHelper authenticationHelper;
    final UserService userService;

    public HomeMvcController(PostService postService, TagService tagService, AuthenticationHelper authenticationHelper, UserService userService) {
        this.postService = postService;
        this.authenticationHelper = authenticationHelper;
        this.userService = userService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping
    public String showHomePage(Model model) {
        model.addAttribute("trending", postService.getAll(new FilterOptionsPosts("desc", "likes")));
        model.addAttribute("recent", postService.getAll(new FilterOptionsPosts("desc", "date")));
        return "HomeView";
    }






}
