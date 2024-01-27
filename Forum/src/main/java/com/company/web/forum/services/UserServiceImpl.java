package com.company.web.forum.services;

import com.company.web.forum.exceptions.BlockedUnblockedUserException;
import com.company.web.forum.exceptions.EntityNotFoundException;
import com.company.web.forum.helpers.FilterOptionsUsers;
import com.company.web.forum.models.Post;
import com.company.web.forum.models.User;
import com.company.web.forum.repositories.PostRepository;
import com.company.web.forum.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService
{
    private final UserRepository repository;
    private final PostRepository postRepository;

    @Autowired
    public UserServiceImpl(UserRepository repository, PostRepository postRepository)
    {
        this.repository = repository;
        this.postRepository = postRepository;
    }

    @Override
    public List<User> getAll(FilterOptionsUsers filterOptionsUsers)
    {
        return repository.getAll(filterOptionsUsers);
    }

    @Override
    public User getById(int id) {
        return repository.getById(id);
    }

    @Override
    public User getByUsername(String username)
    {
        return repository.getByUsername(username);
    }

    @Override
    public User getByEmail(String email)
    {
        return repository.getByEmail(email);
    }

    @Override
    public void addPost(int userId, int postId)
    {
        User user = repository.getById(userId);
        if (user.getPostsByUser().stream().anyMatch(p -> p.getId() == postId))
        {
            return;
        }
        Post post = postRepository.get(postId);
        user.getPostsByUser().add(post);
        repository.update(user);
    }

    @Override
    public void removePost(int userId, int postId)
    {
        User user = repository.getById(userId);
        if (user.getPostsByUser().stream().noneMatch(p -> p.getId() == postId))
        {
            throw new EntityNotFoundException("Post", postId);
        }
        user.getPostsByUser().removeIf(p -> p.getId() == postId);
        repository.update(user);
    }

    @Override
    public void blockUser(int userId)
    {
        User userToBlock = repository.getById(userId);
        if (userToBlock.isBlocked())
        {
            throw new BlockedUnblockedUserException(userId, "blocked");
        }
        userToBlock.setBlocked(true);
        repository.update(userToBlock);
    }

    @Override
    public void unblockUser(int userId)
    {
        User userToUnblock = repository.getById(userId);
        if (!userToUnblock.isBlocked())
        {
            throw new BlockedUnblockedUserException(userId, "unblocked");
        }
        userToUnblock.setBlocked(false);
        repository.update(userToUnblock);
    }
}
