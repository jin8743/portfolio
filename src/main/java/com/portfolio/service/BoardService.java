package com.portfolio.service;

import com.portfolio.domain.Board;
import com.portfolio.repository.board.BoardRepository;
import com.portfolio.request.board.CreateBoard;
import com.portfolio.response.board.BoardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.portfolio.request.board.CreateBoard.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;

    public List<BoardResponse> getList() {
        List<Board> list = boardRepository
                .findAll(Sort.by(Sort.Direction.ASC, "boardName"));

        return list.isEmpty() ? new ArrayList<>() : list.stream().map(BoardResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void create(CreateBoard request) {
        boardRepository.save(createNewBoard(request));
    }
}
