package com.portfolio.service;

import com.portfolio.domain.Board;
import com.portfolio.exception.custom.CustomBadRequestException;
import com.portfolio.repository.board.BoardRepository;
import com.portfolio.request.board.BoardCreateRequest;
import com.portfolio.response.BoardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.portfolio.exception.custom.CustomBadRequestException.*;
import static com.portfolio.repository.util.MemberUtil.*;
import static com.portfolio.request.board.BoardCreateRequest.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;

    public List<BoardResponse> getList() {
        return boardRepository
                .findAll(Sort.by(Sort.Direction.ASC, "boardName"))
                .stream().map(BoardResponse::new).collect(Collectors.toList());
    }

    @Transactional
    public void create(BoardCreateRequest request) {
        validateDuplicate(request.getBoardName());
        Board board = toBoard(request);
        boardRepository.save(board);
    }

    private void validateDuplicate(String boardName) {
        if (boardRepository.existsByBoardName(boardName)) {
            throw new CustomBadRequestException(DUPLICATED_BOARDNAME);
        }

    }
}
