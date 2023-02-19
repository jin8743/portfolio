package com.portfolio.request.board;

import com.portfolio.domain.Board;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class BoardCreateRequest {

    @NotBlank(message = "게시판 이름은 공백일수 없습니다")
    @Size(max = 10, message = "게시판 이름은 10글자 이하로 정해주세요")
    private String boardName;

    @Builder
    public BoardCreateRequest(String boardName) {
        this.boardName = boardName;
    }

    public static Board toBoard(BoardCreateRequest request) {
        return Board.builder()
                .boardName(request.getBoardName())
                .build();
    }
}
