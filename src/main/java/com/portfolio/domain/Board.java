package com.portfolio.domain;

import com.portfolio.exception.custom.PostNotFoundException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Board extends BaseEntity{

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String boardName;

    @Builder
    public Board(String boardName) {
        this.boardName = boardName;
    }

    public static void validateBoard(Board board, String boardName) {
        if (!board.getBoardName().equals(boardName)) {
            throw new PostNotFoundException();
        }
    }
}
