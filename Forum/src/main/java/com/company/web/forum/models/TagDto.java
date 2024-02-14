package com.company.web.forum.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TagDto {
    @NotNull(message = "Tag can't be empty")
    @Size(min = 2, max = 20, message = "Tag must be between 2 and 20 characters long")
    String tagName;


    public TagDto() {

    }

    public String getName() {
        return tagName;
    }

    public void setName(String tagName) {
        this.tagName = tagName;
    }
}
