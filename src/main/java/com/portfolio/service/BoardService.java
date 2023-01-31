package com.portfolio.service;

import com.portfolio.domain.Board;
import com.portfolio.domain.Member;
import com.portfolio.exception.custom.AuthorizationFailedException;
import com.portfolio.exception.custom.MemberNotFoundException;
import com.portfolio.repository.MemberRepository;
import com.portfolio.repository.board.BoardRepository;
import com.portfolio.request.board.BoardCreateRequest;
import com.portfolio.response.BoardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.portfolio.domain.Member.*;
import static com.portfolio.request.board.BoardCreateRequest.*;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    public List<BoardResponse> getList() {
        return boardRepository.getList().stream()
                .map(BoardResponse::new).collect(Collectors.toList());
    }

    @Transactional
    public void create(BoardCreateRequest request, String username) {
        Member member = findMember(username);
        if (isAdmin(member)) {
            Board board = toEntity(request);
            boardRepository.save(board);
        } else {
            throw new AuthorizationFailedException();
        }
    }



    private Member findMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(MemberNotFoundException::new);
    }
}
