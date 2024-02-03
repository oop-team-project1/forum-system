package com.company.web.forum.repositories;


import com.company.web.forum.helpers.FilterOptionsPosts;
import com.company.web.forum.models.Post;
import com.company.web.forum.models.User;

import java.util.List;

public interface PostRepository {
    List<Post> getAll(FilterOptionsPosts filterOptions);
    Post get(int id);
    void create(Post post);
    void update(Post post);
    void delete(int id);
    void deleteMultiple(List<Integer> ids, User user);
    List<Integer> filterNonUserPostIds(List<Integer>ids, User user);
}
