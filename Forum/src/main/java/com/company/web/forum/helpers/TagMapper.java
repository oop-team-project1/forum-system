package com.company.web.forum.helpers;

import com.company.web.forum.models.Tag;
import com.company.web.forum.models.TagDto;
import org.springframework.stereotype.Component;

@Component
public class TagMapper {


    public TagMapper() {
    }

    public Tag fromDto(TagDto dto) {
        Tag tag = new Tag();
        tag.setName(dto.getName());
        return tag;
    }

}
