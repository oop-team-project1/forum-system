package com.company.web.forum.helpers;

import java.time.LocalDate;
import java.util.Optional;

public class FilterOptionsComments {
    private Optional<String> content;
    private Optional<Integer> userId;
    private Optional<String> username;
    private Optional<Integer> postId;
    private Optional<String> postTitle;
    private Optional<LocalDate> startDate;
    private Optional<LocalDate> endDate;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;


    public FilterOptionsComments(String content, Integer userID,
                                 String username, Integer postId,
                                 String postTitle, LocalDate startDate,
                                 LocalDate endDate, String sortBy, String sortOrder) {
        this.content = Optional.ofNullable(content);
        this.userId = Optional.ofNullable(userID);
        this.username = Optional.ofNullable(username);
        this.postId = Optional.ofNullable(postId);
        this.postTitle = Optional.ofNullable(postTitle);
        this.startDate = Optional.ofNullable(startDate);
        this.endDate = Optional.ofNullable(endDate);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }


    public Optional<String> getContent() {
        return content;
    }

    public Optional<Integer> getUserId() {
        return userId;
    }

    public Optional<String> getUsername() {
        return username;
    }

    public Optional<Integer> getPostId() {
        return postId;
    }

    public Optional<String> getPostTitle() {
        return postTitle;
    }

    public Optional<LocalDate> getStartDate() {
        return startDate;
    }

    public Optional<LocalDate> getEndDate() {
        return endDate;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }
}
