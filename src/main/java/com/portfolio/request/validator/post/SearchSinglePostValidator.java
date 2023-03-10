package com.portfolio.request.validator.post;

import com.portfolio.domain.Post;
import com.portfolio.exception.custom.CustomNotFoundException;
import com.portfolio.repository.post.PostRepository;
import com.portfolio.request.post.SearchSinglePost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.portfolio.exception.custom.CustomNotFoundException.*;

@Component
@RequiredArgsConstructor
public class SearchSinglePostValidator implements Validator {

    private final PostRepository postRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SearchSinglePost.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SearchSinglePost request = (SearchSinglePost) target;
        Post post = postRepository.findPostById(request.getId());

        if (post == null) {
            throw new CustomNotFoundException(POST_NOT_FOUND);
        }
    }
}
