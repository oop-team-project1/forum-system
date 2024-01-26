package com.company.web.forum.controllers;

import com.company.web.forum.exceptions.AuthenticationException;
import com.company.web.forum.exceptions.AuthorizationException;
import com.company.web.forum.helpers.AuthenticationHelper;
import com.company.web.forum.models.Post;
import com.company.web.forum.models.User;
import com.company.web.forum.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("tastytale/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public PostController(PostService postService, AuthenticationHelper authenticationHelper) {
        this.authenticationHelper = authenticationHelper;
        this.postService = postService;
    }

    @GetMapping
    public List<Post> get(){return null;}
            /*@RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString,
            @RequestParam(required = false)) {
        User user = authenticationHelper.tryGetUser(encodedString);*/



    @DeleteMapping("/{id}")
    public void delete(@RequestParam int id,
                       @RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString) {
        try {
            User user = authenticationHelper.tryGetUser(encodedString);
            postService.delete(id,user);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        } catch( AuthorizationException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

    }


    @PostMapping("/delete-requests")
    public List<Integer> deleteMultiple(@RequestBody List<Integer> records, @RequestHeader(value = HttpHeaders.AUTHORIZATION) String encodedString) {
        User user;
        try {
            user = authenticationHelper.tryGetUser(encodedString);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        for (int record : records) {
            try {
                postService.delete(record, user);
                //TODO Think if something else can be thrown
                // other than Authentication and EntityNotFound
            } catch (RuntimeException e) {
                records.remove(record);
            }
        }
        return records;


    }
}
