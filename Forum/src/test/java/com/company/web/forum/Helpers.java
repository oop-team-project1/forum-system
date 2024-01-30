package com.company.web.forum;

import com.company.web.forum.helpers.FilterOptionsPosts;
import com.company.web.forum.models.Post;
import com.company.web.forum.models.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Helpers {

    public static Post createMockPost(){
        Post mockPost = new Post();
        mockPost.setId(1);
        mockPost.setTitle("Mock title");
        mockPost.setContent("Mock content");
        mockPost.setCreatedBy(createMockUser());
        return mockPost;

    }

    private static User createMockUser() {
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setFirstName("Mock");
        mockUser.setLastName("Mock");
        mockUser.setUsername("Mock");
        mockUser.setPassword("Mock");
        mockUser.setEmail("Mock@mock.com");
        return mockUser;
    }

    public static FilterOptionsPosts createMockFilterOptionsPosts() {

        List<String> tags = new ArrayList<>();
        tags.add("tag");
        List<String> tagsExclude = new ArrayList<>();
        tagsExclude.add("tagsExclude");
        return new FilterOptionsPosts(
                "author",
                "title",
                "content",
                "keyword",
                LocalDateTime.of(2024, 1, 24, 13, 30),
                LocalDateTime.now(),
                tags,
                tagsExclude,
                "author",
                "desc"

        );
    }
}
