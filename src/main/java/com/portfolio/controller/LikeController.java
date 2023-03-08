package com.portfolio.controller;

import com.portfolio.request.like.CancelLike;
import com.portfolio.request.like.CreateLike;
import com.portfolio.request.like.SearchSinglePostLike;
import com.portfolio.request.validator.like.CreateLikeValidator;
import com.portfolio.request.validator.like.CancelLikeValidator;
import com.portfolio.request.validator.like.SearchSinglePostLikeValidator;
import com.portfolio.response.like.SinglePostLikeResponse;
import com.portfolio.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    private final CreateLikeValidator createLikeValidator;

    private final CancelLikeValidator cancelLikeValidator;

    private final SearchSinglePostLikeValidator searchSinglePostLikeValidator;

    @InitBinder("createLike")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(createLikeValidator);
    }

    @InitBinder("cancelLike")
    public void initBinder2(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(cancelLikeValidator);
    }

    @InitBinder("searchSinglePostLike")
    public void initBinder3(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(searchSinglePostLikeValidator);
    }

    //좋아요 등록
    @PostMapping("/likes")
    public void create(@Validated CreateLike request) {
        likeService.createLike(request);
    }

    //좋아요 삭제
    @DeleteMapping("/likes")
    public void cancel(@Validated CancelLike request) {
        likeService.cancelLike(request);
    }

    //특정 글의 총 좋아요 갯수와 내가 좋아요를 눌렀는지 여부 조회
    @GetMapping("/likes")
    public SinglePostLikeResponse searchPostLikesInfo(@Validated SearchSinglePostLike request) {
        return likeService.searchPostLikes(request);
    }
}
