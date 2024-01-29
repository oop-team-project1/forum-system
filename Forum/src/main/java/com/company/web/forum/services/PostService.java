package com.company.web.forum.services;


import com.company.web.forum.helpers.FilterOptionsPosts;
import com.company.web.forum.models.Post;
import com.company.web.forum.models.User;

import java.util.List;

public interface PostService {
    List<Post> getAll(FilterOptionsPosts filterOptions);
    Post get(int id);
    void create(Post post, User user);
    Post update(int id, User user);
    void delete(int id, User user);
    void deleteMultiple(List<Integer> ids, User user);

}
