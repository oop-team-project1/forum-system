package com.company.web.forum.controllers.mvc;

import com.company.web.forum.exceptions.AuthenticationException;
import com.company.web.forum.exceptions.AuthorizationException;
import com.company.web.forum.exceptions.EntityDuplicateException;
import com.company.web.forum.exceptions.EntityNotFoundException;
import com.company.web.forum.helpers.AuthenticationHelper;
import com.company.web.forum.helpers.FilterOptionsPosts;
import com.company.web.forum.helpers.FilterOptionsUsers;
import com.company.web.forum.helpers.UserMapper;
import com.company.web.forum.models.*;
import com.company.web.forum.services.PostService;
import com.company.web.forum.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/users")
public class UserMvcController {
    private final UserService userService;
    private final PostService postService;
    private final AuthenticationHelper authenticationHelper;
    private final UserMapper userMapper;

    @Autowired
    public UserMvcController(UserService userService, PostService postService, AuthenticationHelper authenticationHelper, UserMapper userMapper) {
        this.userService = userService;
        this.postService = postService;
        this.authenticationHelper = authenticationHelper;
        this.userMapper = userMapper;
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
    public String showAllUsers(@ModelAttribute("filterOptions") FilterDtoUser filterDto, Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetUser(session);
        } catch (AuthenticationException e) {
            return "redirect:/auth/login";
        }

        FilterOptionsUsers filterOptions = new FilterOptionsUsers(
                filterDto.getUsername(),
                filterDto.getFirstName(),
                filterDto.getLastName(),
                filterDto.getEmail(),
                filterDto.getSortBy(),
                filterDto.getSortOrder());

        List<User> users = userService.getAll(filterOptions);
        if (populateIsAuthenticated(session)){
            String currentUsername = (String) session.getAttribute("currentUser");
            model.addAttribute("currentUser", userService.getByEmail(currentUsername));
        }
        model.addAttribute("filterOptionsUsers", filterDto);
        model.addAttribute("users", users);
        return "UsersView";
    }

    @GetMapping("/{id}")
    public String showSingleUser(@PathVariable int id, Model model, HttpSession session) {
        try {
            try {
                authenticationHelper.tryGetUser(session);
            } catch (AuthenticationException e) {
                return "redirect:/auth/login";
            }
            if (populateIsAuthenticated(session)){
                String currentUsername = (String) session.getAttribute("currentUser");
                model.addAttribute("currentUser", userService.getByEmail(currentUsername));
            }

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

    @PostMapping("/blocks/{id}")
    public String updateBlocked(@PathVariable int id, Model model, HttpSession session){
        try {
            User user;
            try {
                user = authenticationHelper.tryGetUser(session);
            } catch (AuthenticationException e) {
                return "redirect:/auth/login";
            }

            User userToBlock = userService.getById(id);
            if(userToBlock.isBlocked()){
                userService.unblockUser(userToBlock.getId(), user);
            } else {
                userService.blockUser(userToBlock.getId(), user);
            }
            return "redirect:/users";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/deletes/{id}")
    public String deleteUser(@PathVariable int id, Model model, HttpSession session){
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationException e) {
            return "redirect:/auth/login";
        }

        try {
            userService.deleteUser(id, user);
            return "redirect:/users";
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

    @PostMapping("/admin/{id}")
    public String makeAdmin(@PathVariable int id, Model model, HttpSession session){
        try {
            User user;
            try {
                user = authenticationHelper.tryGetUser(session);
            } catch (AuthenticationException e) {
                return "redirect:/auth/login";
            }

            User userAdmin = userService.getById(id);
            if(userAdmin.isAdmin()){
                userService.removeAdmin(userAdmin.getId(), user);
            } else {
                userService.makeAdmin(userAdmin.getId(), user);
            }
            return "redirect:/users";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/edit")
    public String showUserEditPage (Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetUser(session);
        } catch (AuthenticationException e) {
            return "redirect:/auth/login";
        }

        if (populateIsAuthenticated(session)){
            String currentUsername = (String) session.getAttribute("currentUser");
            model.addAttribute("currentUser", userService.getByEmail(currentUsername));
        }

        model.addAttribute("userEdit", new UserDtoUpdating());
        return "UserEditView";
    }

    @PostMapping("/edit")
    public String handleUserEdit(@Valid @ModelAttribute("userEdit") UserDtoUpdating userDtoUpdating,
                                 BindingResult bindingResult,
                                 HttpSession session) {
        if(bindingResult.hasErrors()) {
            return "UserEditView";
        }
        try {
            User user;
            try {
                user = authenticationHelper.tryGetUser(session);
            } catch (AuthenticationException e) {
                return "redirect:/auth/login";
            }

            User userToUpdate = userMapper.fromDtoUpdating(userDtoUpdating, user);
            userService.update(userToUpdate, user);
            session.removeAttribute("currentUser");
            session.setAttribute("currentUser", userToUpdate.getEmail());
            return "redirect:/";
        } catch (EntityDuplicateException e) {
            bindingResult.rejectValue("username", "username_error", e.getMessage());
            return "UserEditView";
        }
    }

    @PostMapping("{id}/delete")
    public String deleteMultiplePosts(@PathVariable int id,
            @RequestParam("selectedPosts") List<Integer> selectedPostIds,
                                      HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationException e) {
            return "redirect:/auth/login";
        }
        postService.deleteMultiple(selectedPostIds,user);

        return "redirect:/users/"+id;
    }
}
