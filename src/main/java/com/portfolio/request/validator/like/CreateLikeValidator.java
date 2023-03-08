package com.portfolio.request.validator.like;

import com.portfolio.domain.Post;
import com.portfolio.exception.custom.CustomBadRequestException;
import com.portfolio.exception.custom.CustomNotFoundException;
import com.portfolio.repository.like.LikeRepository;
import com.portfolio.repository.post.PostRepository;
import com.portfolio.repository.util.MemberUtil;
import com.portfolio.request.like.CreateLike;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.portfolio.exception.custom.CustomBadRequestException.DUPLICATED_LIKE;
import static com.portfolio.exception.custom.CustomNotFoundException.*;

@Component
@RequiredArgsConstructor
public class CreateLikeValidator implements Validator {

    private final PostRepository postRepository;

    private final MemberUtil memberUtil;

    private final LikeRepository likeRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(CreateLike.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CreateLike request = (CreateLike) target;
        Post post = postRepository.findPostById(request.getPostId());

        if (post == null) {
            throw new CustomNotFoundException(POST_NOT_FOUND);
        }
        if (likeRepository.pressedLikeOnThisPost(post)) {
            throw new CustomBadRequestException(DUPLICATED_LIKE);
        }
    }
}
