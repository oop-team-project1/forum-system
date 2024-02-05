package com.company.web.forum.services;

import com.company.web.forum.exceptions.AuthorizationException;
import com.company.web.forum.helpers.FilterOptionsComments;
import com.company.web.forum.models.Comment;
import com.company.web.forum.models.Post;
import com.company.web.forum.models.User;
import com.company.web.forum.repositories.CommentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.company.web.forum.Helpers.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    CommentRepository mockRepository;

    @InjectMocks
    CommentServiceImpl commentService;

    @Test
    public void getAll_Should_CallRepository() {
        FilterOptionsComments mockFilterOptions = createMockFilterOptionsComments();
        List<Comment> comments = new ArrayList<>();

        Mockito.when(mockRepository.getAll(mockFilterOptions)).thenReturn(comments);

        Assertions.assertEquals(commentService.getAll(mockFilterOptions), comments);

    }

    @Test
    public void get_Should_ReturnComment_When_MatchByIdExist() {
        Comment mockComment = createMockComment();

        Mockito.when(mockRepository.getById(Mockito.anyInt()))
                .thenReturn(mockComment);

        Comment result = commentService.getById(mockComment.getId());

        Assertions.assertEquals(mockComment, result);
    }

    @Test
    public void create_Should_CallRepository() {
        Comment mockComment = createMockComment();
        User user = createMockUser();
        Post post = createMockPost();

        commentService.create(mockComment, user, post, mockComment);

        Mockito.verify(mockRepository, Mockito.times(1))
                .create(mockComment);
    }

    @Test
    public void create_Should_Throw_When_UserIsBlocked() {

        Comment mockComment = createMockComment();
        User user = createMockUser();
        user.setBlocked(true);
        Post post = createMockPost();

        Assertions.assertThrows(AuthorizationException.class,
                () -> commentService.create(mockComment, user, post, mockComment));
    }

    @Test
    public void update_Should_CallRepository_When_UserIsNotBlockedAndIsAuthor () {
        Comment mockComment = createMockComment();
        User user = createMockUser();

        commentService.update(mockComment, user);

        Mockito.verify(mockRepository, Mockito.times(1))
                .update(mockComment);
    }

    @Test
    public void update_Should_ThrowException_When_UserIsBlocked () {
        Comment mockComment = createMockComment();
        User user = createMockUser();
        user.setBlocked(true);

        Assertions.assertThrows(AuthorizationException.class,
                () -> commentService.update(mockComment, user));
    }

    @Test
    public void update_Should_ThrowException_When_UserIsNotAuthor () {
        Comment mockComment = createMockComment();
        User user = createMockUser();
        user.setId(2);
        Assertions.assertThrows(AuthorizationException.class,
                () -> commentService.update(mockComment, user));
    }

}
