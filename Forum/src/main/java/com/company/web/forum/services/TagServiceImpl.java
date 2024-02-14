package com.company.web.forum.services;

import com.company.web.forum.exceptions.AuthorizationException;
import com.company.web.forum.models.Post;
import com.company.web.forum.models.Tag;
import com.company.web.forum.models.User;
import com.company.web.forum.repositories.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService{

    public static final String PERMISSION_ERROR = "Only post owner or admin can modify tags";
    private final PostService postService;
    private final TagRepository tagRepository;

    public TagServiceImpl(PostService postService,TagRepository tagRepository) {
        this.postService = postService;
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Tag> getAll(int postId) {
      postService.get(postId);
      return tagRepository.getAll(postId);

    }

    @Override
    public List<Tag> getTrending(int amount){
        return tagRepository.getTrending(amount);
    }

    public void create(Tag tag, int postId,User user) {
        Post post = postService.get(postId);
        checkModifyPermissions(user,post);
        tagRepository.create(tag, postId);
    }

    @Override
    public void delete(int tagId, int postId, User user) {
        Post post = postService.get(postId);
        checkModifyPermissions(user, post);
        tagRepository.delete(tagId,postId);
    }


    private static void checkModifyPermissions(User user, Post post) {
        if(post.getCreatedBy()!= user ||!user.isAdmin()){
            throw new AuthorizationException(PERMISSION_ERROR);
        }
    }
}
