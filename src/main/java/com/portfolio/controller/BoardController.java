package com.portfolio.controller;

import com.portfolio.request.board.BoardCreateRequest;
import com.portfolio.response.BoardResponse;
import com.portfolio.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/board")
    public List<BoardResponse> getList() {
        return boardService.getList();
    }

    @PostMapping("/admin/create/board")
    public void createBoard(@RequestBody BoardCreateRequest request) {
        boardService.create(request);
    }
}
