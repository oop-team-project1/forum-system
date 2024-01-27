package com.company.web.forum.repositories;

import com.company.web.forum.helpers.FilterOptionsPosts;
import com.company.web.forum.models.Post;
import com.company.web.forum.models.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostRepositoryImpl implements PostRepository {
    @Override
    public List<Post> getAll(FilterOptionsPosts filterOptions) {
        return null;
    }

    @Override
    public Post get(int id) {
        return null;
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
    public void delete(int id) {

    }
}
