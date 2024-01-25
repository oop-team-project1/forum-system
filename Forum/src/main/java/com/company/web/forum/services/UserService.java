package com.company.web.forum.services;

import com.company.web.forum.models.User;



public interface UserService {

    public User getByUsername(String username);
}
