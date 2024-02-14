package com.company.web.forum.controllers.mvc;

import com.company.web.forum.helpers.FilterOptionsPosts;
import com.company.web.forum.models.Post;
import com.company.web.forum.services.TagService;
import jakarta.servlet.http.HttpSession;
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
    final TagService tagService;

    public HomeMvcController(PostService postService, TagService tagService) {
        this.postService = postService;
        this.tagService = tagService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }
    @GetMapping
    public String showHomePage(Model model) {
        model.addAttribute("trending", postService.getAll(new FilterOptionsPosts("desc","likes")));
        model.addAttribute("recent",postService.getAll(new FilterOptionsPosts("desc","date")));
        model.addAttribute("tags",tagService.getTrending(10));
        return "HomeView";
    }

    @GetMapping("/posts/filterByTag")
    public String filterPostsByTag(@RequestParam("tag") String tag, Model model) {

        List<String> tags = new ArrayList<>();
        tags.add(tag);
        List<Post> filteredPosts = postService.getAll(new FilterOptionsPosts(tags));

        model.addAttribute("posts", filteredPosts);

        return "FilteredPostsView";
    }


}
