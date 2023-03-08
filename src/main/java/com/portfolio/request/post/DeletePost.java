package com.portfolio.request.post;

import lombok.Builder;
import lombok.Getter;

import static com.portfolio.request.validator.ConvertingStringValidator.convertPostId;

@Getter
public class DeletePost {
    private Long id;

    @Builder
    public DeletePost(String id) {
        this.id = convertPostId(id);
    }
}