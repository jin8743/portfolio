package com.portfolio.controller;

import com.portfolio.request.board.BoardCreateRequest;
import com.portfolio.request.validator.board.BoardCreateValidator;
import com.portfolio.response.BoardResponse;
import com.portfolio.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final BoardCreateValidator boardCreateValidator;

    @InitBinder("boardCreateRequest")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(boardCreateValidator);
    }


    /**
     * 글을 작성할떄 어떤 게시판에 작성할지 선택 할수 있도록
     * 모든 게시판 이름 return
     */
    @GetMapping("/write")
    public List<BoardResponse> getList() {
        return boardService.getList();
    }

    /**
     * 게시판 생성
     */
    @PostMapping("/admin/board")
    public void createBoard(@RequestBody BoardCreateRequest request) {
        boardService.create(request);
    }
}
