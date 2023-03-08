package com.portfolio.request.validator.like;

import com.portfolio.exception.custom.CustomNotFoundException;
import com.portfolio.repository.post.PostRepository;
import com.portfolio.request.like.SearchSinglePostLike;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.portfolio.exception.custom.CustomNotFoundException.*;

@Component
@RequiredArgsConstructor
public class SearchSinglePostLikeValidator implements Validator {

    private final PostRepository postRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SearchSinglePostLike.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SearchSinglePostLike request = (SearchSinglePostLike) target;
        Long postId = request.getPostId();

        if (postRepository.existsById(postId) == false) {
            throw new CustomNotFoundException(POST_NOT_FOUND);
        }
    }
}
