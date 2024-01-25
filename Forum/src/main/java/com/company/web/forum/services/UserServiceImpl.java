package com.company.web.forum.services;

import com.company.web.forum.helpers.FilterOptions;
import com.company.web.forum.models.User;
import com.company.web.forum.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService
{
    private final UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository)
    {
        this.repository = repository;
    }

    @Override
    public List<User> getAll()
    {
        return repository.getAll();
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
}
