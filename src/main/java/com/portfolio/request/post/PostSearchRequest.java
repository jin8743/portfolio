package com.portfolio.request.post;

import com.portfolio.exception.custom.PostNotFoundException;
import lombok.Getter;

@Getter
public class PostSearchRequest {

    private String id;

    private Long no;


    public PostSearchRequest(String id, String no) {
        this.id = id;
        this.no = convert(no);
    }

    private Long convert(String no) {
        try {
            return Long.parseLong(no);
        } catch (NumberFormatException e) {
            throw new PostNotFoundException();
        }
    }
}
