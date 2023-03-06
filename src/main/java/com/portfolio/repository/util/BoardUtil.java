package com.portfolio.repository.util;

import com.portfolio.domain.Board;
import com.portfolio.exception.custom.CustomNotFoundException;
import com.portfolio.repository.board.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.portfolio.exception.custom.CustomNotFoundException.*;

@Component
@RequiredArgsConstructor
/** 조회용 Util Class */
public class BoardUtil {


    private final BoardRepository boardRepository;

    public void boardExists(String boardName) {
        if (!boardRepository.existsByBoardName(boardName)) {
            throw new CustomNotFoundException(BOARD_NOT_FOUND);
        }

    }
}
