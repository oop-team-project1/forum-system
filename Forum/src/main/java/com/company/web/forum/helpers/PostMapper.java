package com.company.web.forum.helpers;

import com.company.web.forum.models.Post;
import com.company.web.forum.models.PostDto;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {








    public Post fromDto(PostDto postDto){
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        return post;
    }
}
