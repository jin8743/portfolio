package com.portfolio.request.validator.comment;

import com.portfolio.domain.Post;
import com.portfolio.exception.custom.CustomBadRequestException;
import com.portfolio.exception.custom.CustomNotFoundException;
import com.portfolio.repository.post.PostRepository;
import com.portfolio.request.comment.CreateComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.portfolio.exception.custom.CustomBadRequestException.COMMENTS_NOT_ALLOWED;
import static com.portfolio.exception.custom.CustomNotFoundException.*;

@Component
@RequiredArgsConstructor
public class CreateCommentValidator implements Validator {

    private final PostRepository postRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(CreateComment.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CreateComment request = (CreateComment) target;
        if (request.getPostId() != null) {
            Post post = postRepository.findPostById(request.getPostId());

            if (post == null) {
                throw new CustomNotFoundException(POST_NOT_FOUND);
            }
            if (post.getCommentsAllowed() == false) {
                throw new CustomBadRequestException(COMMENTS_NOT_ALLOWED);
            }
        }
    }
}
