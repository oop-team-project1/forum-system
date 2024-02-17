package com.company.web.forum.controllers.mvc;

import com.company.web.forum.helpers.AuthenticationHelper;
import com.company.web.forum.helpers.FilterOptionsPosts;
import com.company.web.forum.helpers.PostMapper;
import com.company.web.forum.models.FilterOptionsDto;
import com.company.web.forum.models.Post;
import com.company.web.forum.services.CommentService;
import com.company.web.forum.services.PostService;
import com.company.web.forum.services.TagService;
import com.company.web.forum.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

 private final TagService tagService;
    public PostMvcController(PostService postService, CommentService commentService, UserService userService, AuthenticationHelper authenticationHelper, PostMapper postMapper, TagService tagService) {
        this.postService = postService;
        this.commentService = commentService;
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.postMapper = postMapper;
        this.tagService = tagService;
    }

    @GetMapping
    public String showAllBeers(@ModelAttribute("filterOptions") FilterOptionsDto filterDto, Model model, HttpSession session) {
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

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
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
    public String deletePosts(@RequestParam("selectedPosts") List<Integer> selectedPostsIds, HttpSession session,Model model) {
        if (populateIsAuthenticated(session)){
            String currentUsername = (String) session.getAttribute("currentUser");
            model.addAttribute("currentUser", userService.getByUsername(currentUsername));
        }
        postService.deleteMultiple(selectedPostsIds,userService.getByUsername((String) session.getAttribute("currentUser")));
        return "redirect:/posts"; // Redirect to the posts page after deletion
    }
}

