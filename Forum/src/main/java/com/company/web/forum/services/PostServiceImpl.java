package com.company.web.forum.services;

import com.company.web.forum.exceptions.AuthorizationException;
import com.company.web.forum.exceptions.EntityNotFoundException;
import com.company.web.forum.helpers.FilterOptionsPosts;
import com.company.web.forum.models.Post;
import com.company.web.forum.models.User;
import com.company.web.forum.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    public static final String PERMISSION_ERROR = "Only admin or post creator can modify a post";
    public static final String USER_IS_BLOCKED = "Unable to create post, user is blocked";
    private final PostRepository repository;
    private final UserService userService;

    @Autowired
    public PostServiceImpl(PostRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @Override
    public List<Post> getAll(FilterOptionsPosts filterOptions) {
        return repository.getAll(filterOptions);
    }

    @Override
    public Post get(int id) {
        return repository.get(id);
    }

    @Override
    public void create(Post post, User user) {
        checkIfBlocked(user);
        post.setCreatedBy(user);
        repository.create(post);
    }

    @Override
    public void update(Post post, User user) {
        checkIfBlocked(user);
        checkModifyPermissions(post.getId(), user);
        repository.update(post);
    }

    @Override
    public void delete(int id, User user) {
        checkModifyPermissions(id, user);
        repository.delete(id);
    }

    @Override
    public void deleteMultiple(List<Integer> ids, User user) {
        checkModifyPermissions(ids, user);
        repository.deleteMultiple(ids, user);
    }

    private void checkModifyPermissions(List<Integer> ids, User user) {
        List<Integer> nonUserPostIds = repository.filterNonUserPostIds(ids, user);
        if (!(nonUserPostIds.isEmpty())) {
            throw new AuthorizationException(String.format("Trying to modify resources, not belonging to user:%s", nonUserPostIds));
        }
    }


    private void checkModifyPermissions(int id, User user) {
        Post post = repository.get(id);
        if (!(user.isAdmin() || post.getCreatedBy().equals(user))) {
            throw new AuthorizationException(PERMISSION_ERROR);
        }
    }

    private void checkIfBlocked(User user) {
        if (user.isBlocked()) {
            throw new AuthorizationException(USER_IS_BLOCKED);
        }
    }

    @Override
    public Post addUserToLikes(User user, int postId) {
        Post post = repository.get(postId);
        if (post.getUsersWhoLiked().stream().anyMatch(u -> u.equals(user))) {
            return post;
        }
        post.getUsersWhoLiked().add(user);
        repository.update(post);
        return post;
    }

    @Override
    public Post removeUserFromLikes(User user, int postId) {
        Post post = repository.get(postId);
        if (post.getUsersWhoLiked().stream().noneMatch(u -> u.equals(user))) {
            throw new EntityNotFoundException("Can't remove a non existing like");
        }
        post.getUsersWhoLiked().removeIf(u->u.equals(user));
        repository.update(post);
        return post;
    }
}
