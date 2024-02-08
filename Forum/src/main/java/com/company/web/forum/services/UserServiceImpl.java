package com.company.web.forum.services;

import com.company.web.forum.exceptions.AuthorizationException;
import com.company.web.forum.exceptions.BlockedUnblockedUserException;
import com.company.web.forum.exceptions.EntityDuplicateException;
import com.company.web.forum.exceptions.EntityNotFoundException;
import com.company.web.forum.helpers.FilterOptionsUsers;
import com.company.web.forum.models.Post;
import com.company.web.forum.models.User;
import com.company.web.forum.repositories.PostRepository;
import com.company.web.forum.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final PostRepository postRepository;
    public static final String PERMISSION_ERROR = "Only admin or post creator can modify a post";
    public static final String USER_IS_BLOCKED = "User is blocked";

    @Autowired
    public UserServiceImpl(UserRepository repository, PostRepository postRepository) {
        this.repository = repository;
        this.postRepository = postRepository;
    }

    @Override
    public List<User> getAll(FilterOptionsUsers filterOptionsUsers) {
        return repository.getAll(filterOptionsUsers);
    }

    @Override
    public User getById(int id) {
        return repository.getById(id);
    }

    @Override
    public User getByUsername(String username) {
        return repository.getByUsername(username);
    }

    @Override
    public User getByEmail(String email) {
        return repository.getByEmail(email);
    }

    @Override
    public void create(User userToCreate) {
        boolean duplicateExists = true;
        try {
            repository.getByUsername(userToCreate.getUsername());
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }
        if (!isValidEmail(userToCreate.getEmail())) {
            throw new IllegalArgumentException("The email is not valid!");
        }
        if (duplicateExists) {
            throw new EntityDuplicateException("User", "username", userToCreate.getUsername());
        }
        repository.create(userToCreate);
    }

    @Override
    public void update(User userToUpdate, User user) {
        checkIfBlocked(user);
        boolean duplicateExists = true;
        try {
            User existingUser = repository.getByUsername(userToUpdate.getUsername());
            if (existingUser.getId() == userToUpdate.getId()) {
                duplicateExists = false;
            }
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new EntityDuplicateException("User", "username", userToUpdate.getUsername());
        }

        repository.update(userToUpdate);
    }

    @Override
    public void addPost(int userId, int postId, User loggedUser) {
        checkIfBlocked(loggedUser);
        User user = repository.getById(userId);
        if (user.getPostsByUser().stream().anyMatch(p -> p.getId() == postId)) {
            return;
        }
        Post post = postRepository.get(postId);
        user.getPostsByUser().add(post);
        repository.update(user);
    }

    @Override
    public void removePost(int userId, int postId, User loggedUser) {
        checkIfBlocked(loggedUser);
        User user = repository.getById(userId);
        if (user.getPostsByUser().stream().noneMatch(p -> p.getId() == postId)) {
            throw new EntityNotFoundException("Post", postId);
        }
        user.getPostsByUser().removeIf(p -> p.getId() == postId);
        postRepository.delete(postId);
        repository.update(user);
    }

    @Override
    public void blockUser(String username, User user) {
        checkModifyPermissions(user);
        User userToBlock = repository.getByUsername(username);
        try {
            checkIfBlocked(userToBlock);
        } catch (AuthorizationException ignored) {
            throw new BlockedUnblockedUserException(userToBlock.getId(), "blocked");
        }
        userToBlock.setBlocked(true);
        repository.update(userToBlock);
    }

    @Override
    public void unblockUser(String username, User user) {
        checkModifyPermissions(user);
        User userToUnblock = repository.getByUsername(username);
        try {
            checkIfBlocked(userToUnblock);
        } catch (AuthorizationException ignored) {
        }
        userToUnblock.setBlocked(false);
        repository.update(userToUnblock);
    }


    private void checkModifyPermissions(User user) {
        if (!(user.isAdmin())) {
            throw new AuthorizationException(PERMISSION_ERROR);
        }
    }

    private void checkIfBlocked(User user) {
        if (user.isBlocked()) {
            throw new AuthorizationException(USER_IS_BLOCKED);
        }
    }

    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
