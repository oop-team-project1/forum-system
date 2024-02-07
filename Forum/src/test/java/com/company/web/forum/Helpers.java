package com.company.web.forum;

import com.company.web.forum.helpers.FilterOptionsComments;
import com.company.web.forum.helpers.FilterOptionsPosts;
import com.company.web.forum.helpers.FilterOptionsUsers;
import com.company.web.forum.models.Comment;
import com.company.web.forum.models.Post;
import com.company.web.forum.models.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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

    public static User createMockUser() {
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setFirstName("Mock");
        mockUser.setLastName("Mock");
        mockUser.setUsername("Mock");
        mockUser.setPassword("Mock");
        mockUser.setEmail("Mock@mock.com");
        return mockUser;
    }

    public static User createMockAdmin() {
        User mockAdmin = createMockUser();
        mockAdmin.setAdmin(true);
        return mockAdmin;
    }

    public static Comment createMockComment() {
        Comment mockComment = new Comment();
        mockComment.setId(1);
        mockComment.setCommentContent("some content for comment");
        mockComment.setDate_of_creation(LocalDate.of(2024,1,27));
        mockComment.setCreatedBy(createMockUser());
        mockComment.setPost(createMockPost());
        mockComment.setParentComment(mockComment);
        return mockComment;
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

    public static FilterOptionsUsers createMockFilterOptionsUsers(){
        return new FilterOptionsUsers(
                "username",
                "firstName",
                "lastName",
                "email@abv.bg",
                "username",
                "desc");
    }

    public static FilterOptionsComments createMockFilterOptionsComments() {
        return new FilterOptionsComments(
                "content",
                1,
                "username",
                1,
                "postTitle",
                LocalDate.of(2024,1,25),
                LocalDate.of(2024,1,27),
                "date",
                "desc"

        );
    }
}
