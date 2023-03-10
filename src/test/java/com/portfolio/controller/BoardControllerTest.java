package com.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.controller.factory.MemberFactory;
import com.portfolio.domain.Board;
import com.portfolio.repository.board.BoardRepository;
import com.portfolio.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardRepository boardRepository;

    @BeforeEach
    void clear() {
        boardRepository.deleteAll();
    }

    /** 게시판 조회 */
    @DisplayName("게시판 목록 조회")
    @Test
    void test1() throws Exception {
        //given
        List<Board> list = IntStream.rangeClosed(1, 20).mapToObj(i ->
                Board.builder()
                        .boardName("board " + i)
                        .nickname("게시판 " + i)
                        .build()
        ).collect(Collectors.toList());
        boardRepository.saveAll(list);

        //then
        mockMvc.perform(get("/board"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].boardName").value("board 1"))
                .andExpect(jsonPath("$.[0].nickname").value("게시판 1"))
                .andDo(print());
    }

    @DisplayName("게시판이 존재하지 않을 경우 빈 ArrayList 가 반환된다")
    @Test
    void test2() throws Exception {
        mockMvc.perform(get("/board"))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
