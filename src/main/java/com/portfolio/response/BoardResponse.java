package com.portfolio.response;

import com.portfolio.domain.Board;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
public class BoardResponse {

    private final Long id;
    private final String boardName;

    @Builder
    public BoardResponse(Long id, String boardName) {
        this.id = id;
        this.boardName = boardName;
    }

    public BoardResponse(Board board) {
        this.id = board.getId();
        this.boardName = board.getBoardName();
    }
}
