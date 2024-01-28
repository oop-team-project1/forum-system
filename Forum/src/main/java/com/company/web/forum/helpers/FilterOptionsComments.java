package com.company.web.forum.helpers;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

public class FilterOptionsComments {
    private Optional<String> content;
    private Optional<Integer>  userId;
    private Optional<Integer> postId;
    private Optional <Date> startDate;
    private Optional <Date> endDate;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;


    public FilterOptionsComments(String content,
                                 Integer userID,
                                 Integer postId,
                                 Date startDate,
                                 Date endDate,
                                 String sortBy,
                                 String sortOrder) {
        this.content = Optional.ofNullable(content);
        this.userId = Optional.ofNullable(userID);
        this.postId = Optional.ofNullable(postId);
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

    public Optional<Integer> getPostId() {
        return postId;
    }

    public Optional<Date> getStartDate() {
        return startDate;
    }

    public Optional<Date> getEndDate() {
        return endDate;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }
}
