package com.portfolio.request.validator.post;

import com.portfolio.domain.Post;
import com.portfolio.exception.custom.AuthorizationFailedException;
import com.portfolio.exception.custom.CustomNotFoundException;
import com.portfolio.repository.post.PostRepository;
import com.portfolio.request.post.DeletePost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.portfolio.exception.custom.CustomNotFoundException.*;
import static com.portfolio.repository.util.MemberUtil.getAuthenticatedUsername;
import static com.portfolio.repository.util.MemberUtil.isAdmin;

@RequiredArgsConstructor
@Component
public class DeletePostValidator implements Validator{
    private final PostRepository postRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(DeletePost.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        DeletePost request = (DeletePost) target;
        Post post = postRepository.findPostWithMemberById(request.getId());

        if (post == null) {
            throw new CustomNotFoundException(POST_NOT_FOUND);
        }
        if (post.getMember().getUsername().equals(getAuthenticatedUsername()) == false
                && isAdmin() == false) {
            throw new AuthorizationFailedException();
        }
    }
}
