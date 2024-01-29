package com.company.web.forum.helpers;

import com.company.web.forum.models.Post;
import com.company.web.forum.models.PostDto;
import com.company.web.forum.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    private final PostService postService;

    @Autowired
    public PostMapper(PostService postService) {
        this.postService = postService;

    }

    public Post fromDto(int id, PostDto dto) {
        Post post = fromDto(dto);
        post.setId(id);
        Post repositoryPost = postService.get(id);
        post.setCreatedBy(repositoryPost.getCreatedBy());
        return post;
    }

    public Post fromDto(PostDto postDto){
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        return post;
    }
}
