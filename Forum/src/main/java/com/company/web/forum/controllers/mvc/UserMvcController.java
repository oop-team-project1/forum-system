package com.company.web.forum.controllers.mvc;

import com.company.web.forum.exceptions.EntityNotFoundException;
import com.company.web.forum.helpers.FilterOptionsPosts;
import com.company.web.forum.helpers.FilterOptionsUsers;
import com.company.web.forum.models.Post;
import com.company.web.forum.models.User;
import com.company.web.forum.models.UserProfilePic;
import com.company.web.forum.services.PostService;
import com.company.web.forum.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/users")
public class UserMvcController {
    private final UserService userService;
    private final PostService postService;

    @Autowired
    public UserMvcController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @ModelAttribute("posts")
    public List<Post> populateStyles() {
        return postService.getAll(new FilterOptionsPosts());
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping
    public String showAllUsers(Model model, HttpSession session) {
        List<User> users = userService.getAll(new FilterOptionsUsers());
        if (populateIsAuthenticated(session)){
            String currentUsername = (String) session.getAttribute("currentUser");
            model.addAttribute("currentUser", userService.getByUsername(currentUsername));
        }
        model.addAttribute("users", users);
        return "UsersView";
    }

    @GetMapping("/{id}")
    public String showSingleUser(@PathVariable int id, Model model) {
        try {
            User user = userService.getById(id);
            Set<Post> posts = user.getPostsByUser();

            model.addAttribute("user", user);
            model.addAttribute("posts", posts);
            return "UserView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }
}
