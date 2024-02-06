package com.company.web.forum.services;

import static com.company.web.forum.Helpers.*;

import com.company.web.forum.exceptions.AuthorizationException;
import com.company.web.forum.helpers.FilterOptionsPosts;
import com.company.web.forum.models.Post;
import com.company.web.forum.models.User;
import com.company.web.forum.repositories.PostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    PostRepository mockRepository;
    @InjectMocks
    PostServiceImpl postService;


    @Test
    public void getAll_Should_CallRepository() {
        FilterOptionsPosts mockFilterOptions = createMockFilterOptionsPosts();
        List<Post> posts = new ArrayList<>();

        Mockito.when(mockRepository.getAll(mockFilterOptions)).thenReturn(posts);

        Assertions.assertEquals(postService.getAll(mockFilterOptions), posts);

    }

    @Test
    public void get_Should_CallRepository() {
        Post post = createMockPost();

        Mockito.when(mockRepository.get(1)).thenReturn(post);
        //Assert
        Assertions.assertEquals(postService.get(1), post);

    }

    @Test
    public void create_Should_CallRepository_When_UserIsNotBlocked() {
        //Arrange
        Post post = createMockPost();
        User user = createMockUser();

        Mockito.doNothing().when(mockRepository).create(post);
        //Act
        postService.create(post, user);
        //Assert
        Mockito.verify(mockRepository, Mockito.times(1)).create(post);
    }


    @Test
    public void create_Should_Throw_When_UserIsBlocked() {
        //Arrange,Act
        Post post = createMockPost();
        User user = createMockUser();
        user.setBlocked(true);
        //Assert
        Assertions.assertThrows(AuthorizationException.class, () -> postService.create(post, user));
    }

    @Test
    public void update_Should_CallRepository_When_UserCreatedPost() {
        //Arrange
        Post post = createMockPost();
        User user = createMockUser();

        Mockito.doNothing().when(mockRepository).update(post);
        Mockito.when(postService.get(1)).thenReturn(post);
        //Act
        postService.update(post, user);
        //Assert
        Mockito.verify(mockRepository, Mockito.times(1)).update(post);
    }

    @Test
    public void update_Should_Throw_When_UserIsBlocked() {
        //Arrange,Act
        Post post = createMockPost();
        User user = createMockUser();
        user.setBlocked(true);
        //Assert
        Assertions.assertThrows(AuthorizationException.class, () -> postService.update(post, user));
    }

//    @Test
//    public void delete_Should_CallRepository_When_UserIsAdmin() {
//        //Arrange
//        User user = createMockAdmin();
//
//        Mockito.doNothing().when(mockRepository).delete(1);
//        //Act
//        postService.delete(1, user);
//        //Assert
//        Mockito.verify(mockRepository, Mockito.times(1)).delete(1);
//
//    }

    @Test
    public void delete_Should_Throw_When_UserIsNotAdmin() {
        //Arrange,Act
        User anotherUser = createMockUser();
        anotherUser.setId(2);
        Post post = createMockPost();

        Mockito.when(postService.get(1)).thenReturn(post);
        //Assert
        Assertions.assertThrows(AuthorizationException.class, () -> postService.delete(1, anotherUser));
    }

    @Test
    public void multipleDelete_Should_Throw_When_UserNotCreatorOfPost() {
        //Arrange,Act
        User user = createMockUser();

        List<Integer> ids = new ArrayList<>();
        ids.add(1);

        Mockito.when(mockRepository.filterNonUserPostIds(ids, user)).thenReturn(ids);
        //Assert
        Assertions.assertThrows(AuthorizationException.class, () -> postService.deleteMultiple(ids, user));
    }

    @Test
    public void delete_Should_CallRepository_When_UserCreatedAllPosts() {
        //Arrange
        User user = createMockUser();

        List<Integer> ids = new ArrayList<>();

        Mockito.when(mockRepository.filterNonUserPostIds(ids, user)).thenReturn(ids);
        Mockito.doNothing().when(mockRepository).deleteMultiple(ids, user);
        //Act
        postService.deleteMultiple(ids, user);
        //Assert
        Mockito.verify(mockRepository, Mockito.times(1)).deleteMultiple(ids, user);

    }


}
