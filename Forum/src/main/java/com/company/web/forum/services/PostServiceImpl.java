package com.company.web.forum.services;

import com.company.web.forum.exceptions.AuthorizationException;
import com.company.web.forum.helpers.FilterOptionsPosts;
import com.company.web.forum.models.Post;
import com.company.web.forum.models.User;
import com.company.web.forum.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService{

    public static final String PERMISSION_ERROR = "Only admin or post creator can modify a post";
    private final PostRepository repository;

    @Autowired
    public PostServiceImpl(PostRepository repository){
        this.repository = repository;
    }

    @Override
    public List<Post> getAll(FilterOptionsPosts filterOptions) {
        return repository.getAll(filterOptions);
    }

    @Override
    public Post get(int id) {
        return repository.get(id);
    }

    @Override
    public Post create(Post post, User user) {
        return null;
    }

    @Override
    public Post update(int id, User user) {
        return null;
    }

    @Override
    public void delete(int id, User user) {
        checkModifyPermissions(id,user);
        repository.delete(id);
    }

    private void checkModifyPermissions(int id, User user) {
        Post post = repository.get(id);
        if (!(user.isAdmin() || post.getCreatedBy().equals(user))) {
            throw new AuthorizationException(PERMISSION_ERROR);
        }
    }
}
