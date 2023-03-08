package com.portfolio.service;

import com.portfolio.domain.Like;
import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import com.portfolio.repository.like.LikeRepository;
import com.portfolio.repository.post.PostRepository;
import com.portfolio.repository.util.MemberUtil;
import com.portfolio.request.like.CancelLike;
import com.portfolio.request.like.CreateLike;
import com.portfolio.request.like.SearchSinglePostLike;
import com.portfolio.response.like.SinglePostLikeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    private final MemberUtil memberUtil;

    private final PostRepository postRepository;

    /** 좋아요 누름 */
    @Transactional
    public void createLike(CreateLike request) {
        likeRepository.save(createNewLike(request));
    }

    private Like createNewLike(CreateLike request) {
        Member member = memberUtil.getContextMember();
        Post post = postRepository.findPostById(request.getPostId());
        return Like.builder().member(member).post(post).build();
    }

    /** 좋아요 취소 */
    @Transactional
    public void cancelLike(CancelLike request) {
        Like like = findExistingLike(request);
        likeRepository.delete(like);
    }

    private Like findExistingLike(CancelLike request) {
        Post post = postRepository.findPostById(request.getPostId());
        Member member = memberUtil.getContextMember();
        return likeRepository.findByPostAndMember(post, member);
    }

    /** 특정 글에 달린 좋아요 개수와 내가 좋아요를 눌렀는지 조회 */
    public SinglePostLikeResponse searchPostLikes(SearchSinglePostLike request) {
        Post post = postRepository.findPostById(request.getPostId());
        return getSinglePostLikeResponse(post);
    }

    private SinglePostLikeResponse getSinglePostLikeResponse(Post post) {

        //해당 글에 달린 총 좋아요 수
        Long totalLikes = likeRepository.findLikeCountByPost(post);

        //해당 글에 내가 좋아요를 눌렀는지 여부
        Boolean likedPost = likeRepository.pressedLikeOnThisPost(post);
        return new SinglePostLikeResponse(totalLikes, likedPost);
    }
}
