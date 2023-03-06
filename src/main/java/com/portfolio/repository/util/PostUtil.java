package com.portfolio.repository.util;

import com.portfolio.domain.Post;
import com.portfolio.exception.custom.CustomNotFoundException;
import com.portfolio.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.portfolio.exception.custom.CustomNotFoundException.*;

@Component
@RequiredArgsConstructor
public class PostUtil {

    private final PostRepository postRepository;

    public Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomNotFoundException(POST_NOT_FOUND));
    }
}

