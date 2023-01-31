package com.portfolio.domain.util;

import com.portfolio.domain.Comment;
import com.portfolio.request.comment.CommentEditRequest;
import lombok.Builder;
import lombok.Data;

@Data
public class CommentEditor {

    private String content;

    @Builder
    public CommentEditor(String content) {
        this.content = content;
    }

    public static void editComment(CommentEditRequest commentEdit, Comment comment) {
        CommentEditor commentEditor = comment.toEditor()
                .content(commentEdit.getContent())
                .build();

        comment.edit(commentEditor);
    }
}
