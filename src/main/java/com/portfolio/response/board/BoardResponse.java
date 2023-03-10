package com.portfolio.response.board;

import com.portfolio.domain.Board;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardResponse {

    //게시판 번호
    private final Long id;

    //게시판 영문이름
    private final String boardName;

    //게시판 한글 별칭
    private final String nickname;


    public BoardResponse(Board board) {
        this.id = board.getId();
        this.boardName = board.getBoardName();
        this.nickname = board.getNickname();
    }
}
