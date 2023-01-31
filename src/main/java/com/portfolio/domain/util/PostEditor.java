package com.portfolio.domain.util;

import com.portfolio.domain.Post;
import com.portfolio.request.post.PostEditRequest;
import lombok.Builder;
import lombok.Data;

@Data
public class PostEditor {

    private String title;
    private String content;

    @Builder
    public PostEditor(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public static void editPost(PostEditRequest postEdit, Post post) {
        PostEditor postEditor = post.toEditor()
                .title(postEdit.getTitle())
                .content(postEdit.getContent())
                .build();

        post.edit(postEditor);
    }
}
