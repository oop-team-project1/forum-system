package com.company.web.forum.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class CommentDto {
    @NotNull(message = "Content can't be empty")
    @Size(min = 32, max = 8192, message = "The content must be between 32 symbols and 8192 symbols.")
    private String commentContent;

    public CommentDto() {
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
}
