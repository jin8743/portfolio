package com.portfolio.request.post;

import lombok.Getter;

import static com.portfolio.request.validator.ConvertingStringValidator.convertPostId;

/** 수정할 글 조회용 Class */
@Getter
public class SearchPostToEdit {
    private Long id;

    public SearchPostToEdit(String id) {
        this.id = convertPostId(id);
    }
}
