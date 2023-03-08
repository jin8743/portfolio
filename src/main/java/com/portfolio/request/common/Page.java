package com.portfolio.request.common;

import lombok.Builder;
import lombok.Getter;

import static com.portfolio.request.validator.ConvertingStringValidator.convertPage;

@Getter
public class Page {

    private Integer page;

    @Builder
    public Page(String page) {
        this.page = convertPage(page);
    }
}
