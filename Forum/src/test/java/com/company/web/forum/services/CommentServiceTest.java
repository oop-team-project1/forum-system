package com.company.web.forum.services;

import com.company.web.forum.helpers.FilterOptionsComments;
import com.company.web.forum.models.Comment;
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

import static com.company.web.forum.Helpers.createMockComment;
import static com.company.web.forum.Helpers.createMockFilterOptionsComments;

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
    public void get_Should_ReturnComment_When_MatchByIdExist () {
        Comment mockComment = createMockComment();

        Mockito.when(mockRepository.getById(Mockito.anyInt()))
                .thenReturn(mockComment);

        Comment result = commentService.getById(mockComment.getId());

        Assertions.assertEquals(mockComment, result);
    }




}