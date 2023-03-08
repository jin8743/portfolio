package com.portfolio.response;

import com.portfolio.domain.Board;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardResponse {

    private final Long id;
    private final String boardName;
    private final String nickname;


    public BoardResponse(Board board) {
        this.id = board.getId();
        this.boardName = board.getBoardName();
        this.nickname = board.getNickname();
    }
}
