package com.portfolio.request.comment;


import lombok.Getter;

import static com.portfolio.request.validator.ConvertingStringValidator.convertPage;
import static com.portfolio.request.validator.ConvertingStringValidator.convertPostId;

@Getter
public class SearchCommentsInPost {

    private Long id;

    private Integer page;

    public SearchCommentsInPost(String id, String page) {
        this.id = convertPostId(id);
        this.page = convertPage(page);
    }
}
