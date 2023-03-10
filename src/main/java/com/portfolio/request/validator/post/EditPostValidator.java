package com.portfolio.request.validator.post;

import com.portfolio.domain.Post;
import com.portfolio.exception.custom.AuthorizationFailedException;
import com.portfolio.exception.custom.CustomNotFoundException;
import com.portfolio.repository.post.PostRepository;
import com.portfolio.request.post.EditPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.portfolio.exception.custom.CustomNotFoundException.POST_NOT_FOUND;
import static com.portfolio.repository.util.MemberUtil.getAuthenticatedUsername;

@Component
@RequiredArgsConstructor
public class EditPostValidator implements Validator {

    private final PostRepository postRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(EditPost.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EditPost request = (EditPost) target;
        if (request.getPostId() != null) {
            Post post = postRepository.findPostWithMemberById(request.getPostId());

            if (post == null) {
                throw new CustomNotFoundException(POST_NOT_FOUND);
            }
            if (post.getMember().getUsername()
                    .equals(getAuthenticatedUsername()) == false) {
                throw new AuthorizationFailedException();
            }
        }
    }
}
