package com.portfolio.request.validator.comment;

import com.portfolio.domain.Comment;
import com.portfolio.exception.custom.AuthorizationFailedException;
import com.portfolio.exception.custom.CustomNotFoundException;
import com.portfolio.repository.comment.CommentRepository;
import com.portfolio.request.comment.EditCommentIdRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.portfolio.exception.custom.CustomNotFoundException.*;

@RequiredArgsConstructor
@Component
public class EditCommentValidator implements Validator {

    private final CommentRepository commentRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(EditCommentIdRequest.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EditCommentIdRequest request = (EditCommentIdRequest) target;
        Comment comment = commentRepository.findCommentWithMemberById(request.getId());

        if (comment == null) {
            throw new CustomNotFoundException(COMMENT_NOT_FOUND);
        }
        if (comment.getMember().getUsername()
                .equals(getAuthenticatedUsername()) == false) {
            throw new AuthorizationFailedException();
        }
    }

    private String getAuthenticatedUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication().getName();
    }
}
