package com.portfolio.domain.editor;

import com.portfolio.domain.Post;
import com.portfolio.request.post.PostEditRequest;
import lombok.Builder;
import lombok.Data;

@Data
public class PostEditor {
    private String title;
    private String content;
    private Boolean commentsAllowed;

    @Builder
    public PostEditor(String title, String content, Boolean commentsAllowed) {
        this.title = title;
        this.content = content;
        this.commentsAllowed = commentsAllowed;
    }

    public static void editPost(PostEditRequest postEdit, Post post) {
        PostEditor postEditor = post.toEditor()
                .title(postEdit.getTitle())
                .content(postEdit.getContent())
                .commentsAllowed(getCommentsAllowed(postEdit, post))
                .build();

        post.edit(postEditor);
    }


    /** 댓글작성을 허용할지 여부에 대한 변경 요청 정보가 없을 경우 기존 방식 유지 */
    private static Boolean getCommentsAllowed(PostEditRequest request, Post post) {
        return request.getCommentsAllowed() == null ?
                post.getCommentsAllowed() : request.getCommentsAllowed();
    }
}
