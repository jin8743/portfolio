package com.portfolio.controller;

import com.portfolio.request.board.CreateBoard;
import com.portfolio.request.validator.board.BoardCreateValidator;
import com.portfolio.response.board.BoardResponse;
import com.portfolio.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    private final BoardCreateValidator boardCreateValidator;

    @InitBinder("createBoard")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(boardCreateValidator);
    }

    /** 모든 게시판 이름과 별칭 조회 */
    @GetMapping("/board")
    public List<BoardResponse> getList() {
        return boardService.getList();
    }
}
