package com.portfolio.repository.util;

import com.portfolio.domain.Board;
import com.portfolio.exception.custom.BoardNotFoundException;
import com.portfolio.repository.board.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BoardUtil {

    private final BoardRepository boardRepository;

    public Board getBoard(String boardName) {
        return boardRepository.findByBoardName(boardName).orElseThrow(BoardNotFoundException::new);
    }
}
