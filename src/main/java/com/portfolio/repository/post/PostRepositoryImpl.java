package com.portfolio.repository.post;

import com.portfolio.domain.*;
import com.portfolio.request.post.SearchPostsByBoard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.portfolio.domain.Post.*;
import static com.portfolio.domain.QBoard.*;
import static com.portfolio.domain.QComment.*;
import static com.portfolio.domain.QMember.*;
import static com.portfolio.domain.QPost.*;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    /** 글 단건 조회 */
    @Override
    public Post findSinglePostWithId(Long postId) {

        return jpaQueryFactory
                .selectDistinct(post)
                .from(post)
                .where(post.id.eq(postId))
                .leftJoin(post.member, member).fetchJoin()
                .leftJoin(post.board, board).fetchJoin()
                .leftJoin(post.comments, comment).fetchJoin()
                .leftJoin(comment.member, member).fetchJoin()
//                .leftJoin(comment.parent).fetchJoin()
                .fetchOne();
    }


    /** 특정 게시판에 작성된 글 페이징 조회 */
    @Override
    public List<Post> findPostsByBoard(SearchPostsByBoard searchRequest) {
        return jpaQueryFactory
                .selectFrom(post)
                .where(post.board.boardName.eq(searchRequest.getBoard()))
                .leftJoin(post.member, member).fetchJoin()
                .leftJoin(post.board, board).fetchJoin()
                .orderBy(post.id.desc())
                .offset(searchRequest.getOffset())
                .limit(searchRequest.getSize())
                .fetch();
    }

    /** 특정 member 가 작성한 글 페이징 조회 */
    @Override
    public List<Post> findPostsByMember(Member member, int page) {
        return jpaQueryFactory
                .selectFrom(post)
                .where(post.member.eq(member))
                .join(post.board, board).fetchJoin()
                .orderBy(post.id.desc())
                .offset(getOffset(page))
                .limit(20)
                .fetch();
    }

    private Long getOffset(int page) {
        return (page - 1) * 20L;
    }
}
