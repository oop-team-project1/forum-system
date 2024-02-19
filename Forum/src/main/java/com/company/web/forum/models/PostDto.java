package com.company.web.forum.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class PostDto {
    @NotNull
    @Size(min = 16, max = 64, message = "Title must be between 16 and 64 symbols")
    private String title;
    @NotNull
    @Size(min = 32, max = 8192, message = "The content must be between 32 and 8192 symbols")
    private String content;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Size(min=20, max = 150, message = "Description must be between 20 and 150 symbols")
    private String description;

    public PostDto() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
