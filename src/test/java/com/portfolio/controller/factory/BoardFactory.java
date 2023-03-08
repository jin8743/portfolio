package com.portfolio.controller.factory;

import com.portfolio.domain.Board;
import com.portfolio.repository.board.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BoardFactory {

    @Autowired
    private  BoardRepository boardRepository;

    public Board createBoard(String boardName) {
        Board board = Board.builder()
                .boardName(boardName)
                .nickname("자유게시판 " + boardName)
                .build();
        boardRepository.save(board);
        return board;
    }
}
