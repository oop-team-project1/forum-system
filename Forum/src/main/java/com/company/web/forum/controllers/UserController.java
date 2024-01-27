package com.company.web.forum.controllers;

import com.company.web.forum.exceptions.EntityNotFoundException;
import com.company.web.forum.helpers.AuthenticationHelper;
import com.company.web.forum.helpers.FilterOptionsUsers;
import com.company.web.forum.helpers.UserMapper;
import com.company.web.forum.models.User;
import com.company.web.forum.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("tastytale/api/v1/users")
public class UserController
{
    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper, AuthenticationHelper authenticationHelper)
    {
        this.userService = userService;
        this.userMapper = userMapper;
        this.authenticationHelper = authenticationHelper;
    }
    @GetMapping
    public List<User> get(@RequestParam(required = false) String username,
                          @RequestParam(required = false) String firstName,
                          @RequestParam(required = false) String lastName,
                          @RequestParam(required = false) String email,
                          @RequestParam(required = false) String sortBy,
                          @RequestParam(required = false) String sortOrder)
    {
        FilterOptionsUsers filterOptionsUsers = new FilterOptionsUsers(username, firstName, lastName,
                                                                            email, sortBy, sortOrder);
        return userService.getAll(filterOptionsUsers);
    }

    @GetMapping("/{id}")
    public User get(@PathVariable int id)
    {
        try
        {
            return userService.getById(id);
        }
        catch (EntityNotFoundException e)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    @GetMapping("/username")
    public User getByUsername(@RequestParam String username)
    {
        try
        {
            return userService.getByUsername(username);
        }
        catch (EntityNotFoundException e)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
    @GetMapping("/email")
    public User getByEmail(@RequestParam String email)
    {
        try
        {
            return userService.getByEmail(email);
        }
        catch (EntityNotFoundException e)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
