package com.company.web.forum.repositories;


import com.company.web.forum.helpers.FilterOptionsPosts;
import com.company.web.forum.models.Post;
import com.company.web.forum.models.User;

import java.util.List;

public interface PostRepository {
    List<Post> getAll(FilterOptionsPosts filterOptions);
    Post get(int id);
    Post create(Post post, User user);
    Post update(int id,User user);
    void delete(int id);
}
