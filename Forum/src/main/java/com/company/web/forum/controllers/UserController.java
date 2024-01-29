package com.company.web.forum.controllers;

import com.company.web.forum.exceptions.*;
import com.company.web.forum.helpers.AuthenticationHelper;
import com.company.web.forum.helpers.FilterOptionsUsers;
import com.company.web.forum.helpers.UserMapper;
import com.company.web.forum.models.*;
import com.company.web.forum.services.UserService;
import jakarta.validation.Valid;
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
    public User get(@PathVariable int id,
                    @RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString)
    {
        try
        {
            User user = authenticationHelper.tryGetUser(encodedString);
            checkAccessPermissions(id, user);
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

    @PostMapping
    public User create(@Valid @RequestBody UserDto userDto)
    {
        try
        {
            User userToCreate = userMapper.fromDto(userDto);
            userService.create(userToCreate);
            return userToCreate;
        }
        catch (EntityDuplicateException e)
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public User update(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString,
                       @PathVariable int id,
                       @Valid @RequestBody UserDto userDto) {
        try
        {
            User user = authenticationHelper.tryGetUser(encodedString);
            User userToUpdate = userMapper.fromDto(id, userDto);
            checkAccessPermissions(id, user);
            userService.update(userToUpdate);
            return userToUpdate;
        }
        catch (EntityNotFoundException e)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        catch (EntityDuplicateException e)
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
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
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
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
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    private static void checkAccessPermissions(int targetUserId, User executingUser)
    {
        if (!executingUser.isBlocked() && executingUser.getId() != targetUserId)
        {
            throw new AuthorizationException(ERROR_MESSAGE);
        }
    }

    private static void checkAccessAdminPermissions(int targetUserId, User executingUser)
    {
        if (!executingUser.isAdmin() && !executingUser.isBlocked() && executingUser.getId() != targetUserId)
        {
            throw new AuthorizationException(ERROR_MESSAGE_ADMIN);
        }
    }
}
