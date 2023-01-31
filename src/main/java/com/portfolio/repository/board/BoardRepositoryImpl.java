package com.portfolio.repository.board;

import com.portfolio.domain.Board;
import com.portfolio.repository.board.BoardRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.portfolio.domain.QBoard.*;

@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Board> getList() {
        return jpaQueryFactory
                .selectFrom(board)
                .orderBy(board.boardName.asc())
                .fetch();
    }
}
