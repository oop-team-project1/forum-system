package com.company.web.forum.controllers;

import com.company.web.forum.models.User;
import com.company.web.forum.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("tastytale/api/v1/users")
public class UserController
{
    private final UserService userService;

    @Autowired
    public UserController(UserService userService)
    {
        this.userService = userService;
    }
    @GetMapping
    public List<User> get()
    {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User get(@PathVariable int id)
    {
        return userService.getById(id);
    }
    @GetMapping("/username/{username}")
    public User getByUsername(@PathVariable String username)
    {
        return userService.getByUsername(username);
    }
    @GetMapping("/email/{email}")
    public User getByEmail(@PathVariable String email)
    {
        return userService.getByEmail(email);
    }
}
