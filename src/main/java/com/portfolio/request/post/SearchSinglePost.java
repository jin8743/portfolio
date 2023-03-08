package com.portfolio.request.post;

import lombok.Builder;
import lombok.Getter;

import static com.portfolio.request.validator.ConvertingStringValidator.convertPostId;

@Getter
public class SearchSinglePost {

    private Long id;

    @Builder
    public SearchSinglePost(String id) {
        this.id = convertPostId(id);
    }
}
