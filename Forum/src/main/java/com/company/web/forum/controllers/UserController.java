package com.company.web.forum.controllers;

import com.company.web.forum.exceptions.AuthenticationException;
import com.company.web.forum.exceptions.AuthorizationException;
import com.company.web.forum.exceptions.BlockedUnblockedUserException;
import com.company.web.forum.exceptions.EntityNotFoundException;
import com.company.web.forum.helpers.AuthenticationHelper;
import com.company.web.forum.helpers.FilterOptionsUsers;
import com.company.web.forum.helpers.UserMapper;
import com.company.web.forum.models.User;
import com.company.web.forum.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("tastytale/api/v1/users")
public class UserController
{
    public static final String ERROR_MESSAGE = "You are not authorized to browse user information.";
    public static final String ERROR_MESSAGE_ADMIN = "You are not authorized to browse admin information.";
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
    @GetMapping("/{id}/username")
    public User getByUsername(@PathVariable int id,
                              @RequestParam String username,
                              @RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString)
    {
        try
        {
            User user = authenticationHelper.tryGetUser(encodedString);
            checkAccessAdminPermissions(id, user);
            return userService.getByUsername(username);
        }
        catch (EntityNotFoundException e)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        catch (AuthorizationException e)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
    @GetMapping("/{id}/email")
    public User getByEmail(@PathVariable int id,
                           @RequestParam String email,
                           @RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString)
    {
        try
        {
            User user = authenticationHelper.tryGetUser(encodedString);
            checkAccessAdminPermissions(id, user);
            return userService.getByEmail(email);
        }
        catch (EntityNotFoundException e)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        catch (AuthorizationException e)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
    @PutMapping("/{id}/posts/{postId}")
    public void addPost(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString,
                        @PathVariable int id,
                        @PathVariable int postId)
    {
        try
        {
            User user = authenticationHelper.tryGetUser(encodedString);
            checkAccessPermissions(id, user);
            userService.addPost(id, postId);
        }
        catch (EntityNotFoundException e)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        catch (AuthorizationException e)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/{id}/posts/{postId}")
    public void removePost(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString,
                           @PathVariable int id,
                           @PathVariable int postId)
    {
        try
        {
            User user = authenticationHelper.tryGetUser(encodedString);
            checkAccessPermissions(id, user);
            userService.removePost(id, postId);
        }
        catch (EntityNotFoundException e)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        catch (AuthorizationException e)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{id}/blockUser/{userId}")
    public void blockUser(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString,
                          @PathVariable int id,
                          @PathVariable int userId)
    {
        try
        {
            User user = authenticationHelper.tryGetUser(encodedString);
            checkAccessAdminPermissions(id, user);
            userService.blockUser(userId);
        }
        catch (EntityNotFoundException e)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        catch (AuthorizationException e)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
        catch (BlockedUnblockedUserException e)
        {
            throw new ResponseStatusException(HttpStatus.ALREADY_REPORTED, e.getMessage());
        }
    }

    @PutMapping("/{id}/unblockUser/{userId}")
    public void unblockUser(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString,
                          @PathVariable int id,
                          @PathVariable int userId)
    {
        try
        {
            User user = authenticationHelper.tryGetUser(encodedString);
            checkAccessAdminPermissions(id, user);
            userService.unblockUser(userId);
        }
        catch (EntityNotFoundException e)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        catch (AuthorizationException e)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
        catch (BlockedUnblockedUserException e)
        {
            throw new ResponseStatusException(HttpStatus.ALREADY_REPORTED, e.getMessage());
        }
    }

    private static void checkAccessPermissions(int targetUserId, User executingUser)
    {
        if (!executingUser.isBlocked() && executingUser.getId() != targetUserId)
        {
            throw new AuthenticationException(ERROR_MESSAGE);
        }
    }

    private static void checkAccessAdminPermissions(int targetUserId, User executingUser)
    {
        if (!executingUser.isAdmin() && !executingUser.isBlocked() && executingUser.getId() != targetUserId)
        {
            throw new AuthenticationException(ERROR_MESSAGE);
        }
    }
}
