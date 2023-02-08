package com.portfolio.response;

import com.portfolio.domain.Board;
import lombok.Builder;

import java.time.LocalDateTime;

public class BoardResponse {

    private final String boardName;
    @Builder
    public BoardResponse(Long id, String boardName) {
        this.boardName = boardName;
    }

    public BoardResponse(Board board) {
        this.boardName = board.getBoardName();
    }
}
