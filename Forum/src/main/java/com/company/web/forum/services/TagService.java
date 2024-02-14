package com.company.web.forum.services;

import com.company.web.forum.models.Post;
import com.company.web.forum.models.Tag;
import com.company.web.forum.models.User;

import java.util.List;


public interface TagService {

    List<Tag> getAll(int postId);
    List<Tag> getTrending(int amount);
    void create(Tag tag, int post_id, User user);
    void delete(int tagId, int postId ,User user);


}
