package com.portfolio.response;

import com.portfolio.domain.Board;
import lombok.Builder;

import java.time.LocalDateTime;

public class BoardResponse {

    private final Long id;
    private final String boardName;
    private final LocalDateTime lastModifiedDate;

    @Builder
    public BoardResponse(Long id, String boardName, LocalDateTime lastModifiedDate) {
        this.id = id;
        this.boardName = boardName;
        this.lastModifiedDate = lastModifiedDate;
    }

    public BoardResponse(Board board) {
        this.id = board.getId();
        this.boardName = board.getBoardName();
        this.lastModifiedDate = board.getLastModifiedDate();
    }
}
