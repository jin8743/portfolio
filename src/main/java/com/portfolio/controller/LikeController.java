package com.portfolio.controller;

import com.portfolio.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    //좋아요 등록
    @PostMapping("/likes/{postId}")
    public void create(@PathVariable Long postId) {
        likeService.like(postId);
    }

//    @GetMapping("/comments/liked")
//    public List<>
}
