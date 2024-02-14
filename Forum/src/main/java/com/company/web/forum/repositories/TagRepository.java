package com.company.web.forum.repositories;

import com.company.web.forum.models.Post;
import com.company.web.forum.models.Tag;

import java.util.List;


public interface TagRepository {
    void create(Tag tag, int postId);

    void delete(int tagId, int postId);

    Tag get(int tagId);

    List<Tag> getAll(int postId);

    List<Tag> getTrending(int amount);

}
