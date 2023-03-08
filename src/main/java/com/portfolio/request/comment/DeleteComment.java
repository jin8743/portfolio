package com.portfolio.request.comment;

import lombok.Builder;
import lombok.Getter;

import static com.portfolio.request.validator.ConvertingStringValidator.convertCommentId;

@Getter
public class DeleteComment {
    private Long id;

    @Builder
    public DeleteComment(String id) {
        this.id = convertCommentId(id);
    }
}
