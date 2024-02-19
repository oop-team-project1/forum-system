package com.company.web.forum.services;

import com.company.web.forum.exceptions.*;
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
    public static final String PERMISSION_ERROR = "Only admin or post creator can modify a post";
    public static final String USER_IS_BLOCKED = "User is blocked";

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
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
        boolean duplicateUsernameExists = true;
        boolean duplicateEmailExists = true;

        try {
            repository.getByUsername(userToCreate.getUsername());
        } catch (EntityNotFoundException e) {
            duplicateUsernameExists = false;
        }

        try {
            repository.getByEmail(userToCreate.getEmail());
        } catch (EntityNotFoundException e) {
            duplicateEmailExists = false;
        }

        if (!isValidEmail(userToCreate.getEmail())) {
            throw new IllegalArgumentException("The email is not valid!");
        }

        if (duplicateUsernameExists) {
            throw new EntityDuplicateException("User", "username", userToCreate.getUsername());
        }

        if (duplicateEmailExists) {
            throw new EntityDuplicateException("User", "email", userToCreate.getEmail());
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
    public void blockUser(int id, User user) {
        checkModifyPermissions(user);
        User userToBlock = repository.getById(id);
        try {
            checkIfBlocked(userToBlock);
        } catch (AuthorizationException ignored) {
            throw new BlockedUnblockedUserException(userToBlock.getId(), "blocked");
        }
        userToBlock.setBlocked(true);
        repository.update(userToBlock);
    }

    @Override
    public void unblockUser(int id, User user) {
        checkModifyPermissions(user);
        User userToUnblock = repository.getById(id);
        try {
            checkIfBlocked(userToUnblock);
        } catch (AuthorizationException ignored) {
            userToUnblock.setBlocked(false);
            repository.update(userToUnblock);
            return;
        }
        throw new BlockedUnblockedUserException(userToUnblock.getId(), "unblocked");
    }

    @Override
    public void makeAdmin(int id, User user) {
        checkModifyPermissions(user);
        User userToAdmin = repository.getById(id);
        try {
            checkModifyPermissions(userToAdmin);
        } catch (AuthorizationException ignored) {
            userToAdmin.setAdmin(true);
            repository.update(userToAdmin);
            return;
        }
        throw new AdminException(userToAdmin.getId(), "admin");
    }

    @Override
    public void removeAdmin(int id, User user) {
        checkModifyPermissions(user);
        User userToAdmin = repository.getById(id);
        try {
            checkModifyPermissions(userToAdmin);
        } catch (AuthorizationException e) {
            throw new AdminException(userToAdmin.getId(), "removed admin");
        }
        userToAdmin.setAdmin(false);
        repository.update(userToAdmin);
    }

    @Override
    public void deleteUser(int id, User user) {
        checkModifyPermissions(user);
        repository.deleteUser(id);
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
