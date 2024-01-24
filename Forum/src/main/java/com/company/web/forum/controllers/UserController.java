package com.company.web.forum.controllers;

import com.company.web.forum.models.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("tastytale/api/v1/users")
public class UserController {

    @GetMapping
    public List<User> get(){
        return null;
    }


}
