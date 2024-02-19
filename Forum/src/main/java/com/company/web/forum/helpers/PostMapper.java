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

    public Post fromDto(PostDto postDto) {
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setDescription(postDto.getDescription());
        return post;
    }

    public Post fromDtoUpdating(PostDto postDto, Post post) {
        Post newPost = new Post();
        newPost.setId(post.getId());
        newPost.setTitle(postDto.getTitle());
        newPost.setContent(postDto.getContent());
        newPost.setDescription(postDto.getDescription());
        newPost.setCreatedBy(post.getCreatedBy());
        newPost.setLikes(post.getLikes());
        newPost.setUsersWhoLiked(post.getUsersWhoLiked());
        newPost.setComments(post.getComments());
        newPost.setTags(post.getTags());


        return newPost;
    }

    public PostDto toDto(Post post) {
        PostDto postDto = new PostDto();
        postDto.setTitle(post.getTitle());
        postDto.setContent(post.getContent());
        postDto.setDescription(post.getDescription());
        return postDto;
    }
}
