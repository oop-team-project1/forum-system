package com.company.web.forum.services;

import static com.company.web.forum.Helpers.*;
import com.company.web.forum.helpers.FilterOptionsPosts;
import com.company.web.forum.models.Post;
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
@ExtendWith(MockitoExtension .class)
public class PostServiceTest {

    @Mock
    PostRepository mockRepository;
    @InjectMocks
    PostServiceImpl postService;


    @Test
    public void getAll_Should_CallRepository() {
        FilterOptionsPosts mockFilterOptions = createMockFilterOptionsPosts();
        List<Post> posts =new ArrayList<>();

        Mockito.when(mockRepository.getAll(mockFilterOptions)).thenReturn(posts);

        Assertions.assertEquals(postService.getAll(mockFilterOptions), posts);

    }

    @Test
    public void create_Should_CallRepository_When_UserIsNotBlocked(){


    }




}
