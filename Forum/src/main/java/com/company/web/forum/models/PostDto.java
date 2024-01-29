package com.company.web.forum.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PostDto {
    @NotNull
    @Size(min = 16, max = 64, message = "Title must be between 16 and 64 symbols")
    String title;

    @NotNull
    @Size(min = 32, max = 8192, message = "The content must be between 32 and 8192 symbols")
    String content;


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
