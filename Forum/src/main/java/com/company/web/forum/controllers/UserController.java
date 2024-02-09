package com.company.web.forum.controllers;

import com.company.web.forum.exceptions.*;
import com.company.web.forum.helpers.AuthenticationHelper;
import com.company.web.forum.helpers.FilterOptionsUsers;
import com.company.web.forum.helpers.UserMapper;
import com.company.web.forum.models.*;
import com.company.web.forum.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("tastytale/api/v1/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper, AuthenticationHelper authenticationHelper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    @Operation(
            tags = {"User API"},
            summary = "Get users with filters",
            description = "Retrieves a list of users based on specified filter options.",
            security = {@SecurityRequirement(name = "basic")}
    )
    public List<User> get(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString,
                          @RequestParam(required = false) String username,
                          @RequestParam(required = false) String firstName,
                          @RequestParam(required = false) String lastName,
                          @RequestParam(required = false) String email,
                          @RequestParam(required = false) String sortBy,
                          @RequestParam(required = false) String sortOrder) {
        FilterOptionsUsers filterOptionsUsers = new FilterOptionsUsers(username, firstName, lastName,
                email, sortBy, sortOrder);
        try {
            authenticationHelper.tryGetUser(encodedString);
            return userService.getAll(filterOptionsUsers);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(
            tags = {"User API"},
            summary = "Get user by ID",
            description = "Retrieves a user based on the provided ID.",
            security = {@SecurityRequirement(name = "basic")}
    )
    public User get(@PathVariable int id,
                    @RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString) {
        try {
            authenticationHelper.tryGetUser(encodedString);
            return userService.getById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/username")
    @Operation(
            tags = {"User API"},
            summary = "Get user by username",
            description = "Retrieves a user based on the provided username.",
            security = {@SecurityRequirement(name = "basic")}
    )
    public User getByUsername(@RequestParam String username,
                              @RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString) {
        try {
            authenticationHelper.tryGetUser(encodedString);
            return userService.getByUsername(username);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @GetMapping("/email")
    @Operation(
            tags = {"User API"},
            summary = "Get user by email",
            description = "Retrieves a user based on the provided email.",
            security = {@SecurityRequirement(name = "basic")}
    )
    public User getByEmail(@RequestParam String email,
                           @RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString) {
        try {
            authenticationHelper.tryGetUser(encodedString);
            return userService.getByEmail(email);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PostMapping
    @Operation(
            tags = {"User API"},
            summary = "Create a new user",
            description = "Creates a new user."
    )
    public User create(@Valid @RequestBody UserDto userDto) {
        try {
            User userToCreate = userMapper.fromDto(userDto);
            userService.create(userToCreate);
            return userToCreate;
        } catch (EntityDuplicateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(
            tags = {"User API"},
            summary = "Update user information",
            description = "Updates user information based on the provided ID.",
            security = {@SecurityRequirement(name = "basic")}
    )
    public User update(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString,
                       @PathVariable int id,
                       @Valid @RequestBody UserDto userDto) {
        try {
            User user = authenticationHelper.tryGetUser(encodedString);
            User userToUpdate = userMapper.fromDto(id, userDto);
            userService.update(userToUpdate, user);
            return userToUpdate;
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (EntityDuplicateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthorizationException | AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{id}/posts/{postId}")
    @Operation(
            tags = {"User API"},
            summary = "Add a post to a user's account",
            description = "Adds a specific post to the user's account.",
            security = {@SecurityRequirement(name = "basic")}
    )
    public void addPost(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString,
                        @PathVariable int id,
                        @PathVariable int postId) {
        try {
            User user = authenticationHelper.tryGetUser(encodedString);
            userService.addPost(id, postId, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @DeleteMapping("/{id}/posts/{postId}")
    @Operation(
            tags = {"User API"},
            summary = "Remove a post from a user's account",
            description = "Removes a specific post from the user's account.",
            security = {@SecurityRequirement(name = "basic")}
    )
    public void removePost(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString,
                           @PathVariable int id,
                           @PathVariable int postId) {
        try {
            User user = authenticationHelper.tryGetUser(encodedString);
            userService.removePost(id, postId, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/blocks/{id}")
    @Operation(
            tags = {"User (Admin) API"},
            summary = "Block a user",
            description = "Blocks a user based on the provided username.",
            security = {@SecurityRequirement(name = "basic")}
    )
    public void blockUser(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString,
                          @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(encodedString);
            userService.blockUser(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (BlockedUnblockedUserException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @DeleteMapping("/blocks/{id}")
    @Operation(
            tags = {"User (Admin) API"},
            summary = "Unblock a user",
            description = "Unblocks a user based on the provided username.",
            security = {@SecurityRequirement(name = "basic")}

    )
    public void unblockUser(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString,
                            @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(encodedString);
            userService.unblockUser(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (BlockedUnblockedUserException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PutMapping("/admin/{id}")
    public void makeAdmin(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString,
                          @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(encodedString);
            userService.makeAdmin(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (AdminException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @DeleteMapping("/admin/{id}")
    public void removeAdmin(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString,
                          @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(encodedString);
            userService.removeAdmin(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (AdminException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }
}
