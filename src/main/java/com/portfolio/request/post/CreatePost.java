package com.portfolio.request.post;

import com.portfolio.domain.Board;
import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class CreatePost {

    @NotBlank(message = "글을 작성할 게시판을 선택하세요")
    private String boardName;

    @NotBlank(message = "제목을 입력하세요")
    @Size(max = 30, message = "제목은 30글자 이하로 작성하세요")
    private String title;

    @NotBlank(message = "내용을 입력하세요")
    private String content;

    private Boolean commentsAllowed;

    @Builder
    public CreatePost(String boardName, String title, String content, Boolean commentsAllowed) {
        this.boardName = boardName;
        this.title = title;
        this.content = content;
        this.commentsAllowed = commentsAllowed == null || commentsAllowed;
    }

    public static Post createPost(Member member, Board board, CreatePost request) {
        return Post.builder()
                .member(member)
                .board(board)
                .title(request.getTitle())
                .content(request.getContent())
                .commentsAllowed(request.getCommentsAllowed())
                .build();
    }

}
