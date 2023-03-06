package com.portfolio.repository.post;

import com.portfolio.domain.*;
import com.portfolio.request.post.BoardSearchRequest;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.portfolio.domain.Post.*;
import static com.portfolio.domain.QBoard.*;
import static com.portfolio.domain.QComment.*;
import static com.portfolio.domain.QMember.*;
import static com.portfolio.domain.QPost.*;
import static java.lang.Math.*;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    /** 글 단건 조회 */
    @Override
    public Post findAllWithId(Long postId) {

        Post findPost = jpaQueryFactory
                .selectDistinct(post)
                .from(post)
                .where(post.id.eq(postId))
                .leftJoin(post.member, member).fetchJoin()
                .leftJoin(post.board, board).fetchJoin()
                .leftJoin(post.comments, comment).fetchJoin()
                .leftJoin(comment.member, member).fetchJoin()
                .orderBy(comment.id.asc())
                .fetchOne();

        checkNull(findPost);
        return findPost;
    }


    /** 특정 게시판에 작성된 글 페이징 조회 */
    @Override
    public List<Post> boardList(BoardSearchRequest searchRequest) {
        return jpaQueryFactory
                .selectFrom(post)
                .where(post.board.boardName.eq(searchRequest.getBoard()))
                .leftJoin(post.member, member).fetchJoin()
                .leftJoin(post.board, board).fetchJoin()
                .orderBy(post.id.desc())
                .offset(searchRequest.getOffset())
                .limit(searchRequest.getList_num())
                .fetch();
    }

    @Override
    public List<Post> memberList(Member member, int page) {
        return jpaQueryFactory
                .selectFrom(post)
                .where(post.member.eq(member))
                .join(post.board, board).fetchJoin()
                .orderBy(post.id.desc())
                .offset(getOffset(page))
                .limit(20)
                .fetch();
    }

    // page 에 음수가 들어갔을 경우 예외처리
    private Long getOffset(int page) {
        return (max(page, 1) - 1) * 20L;
    }

}
