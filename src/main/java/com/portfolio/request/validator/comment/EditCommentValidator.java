package com.portfolio.request.validator.comment;

import com.portfolio.domain.Comment;
import com.portfolio.exception.custom.AuthorizationFailedException;
import com.portfolio.exception.custom.CustomNotFoundException;
import com.portfolio.repository.comment.CommentRepository;
import com.portfolio.request.comment.EditComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.portfolio.exception.custom.CustomNotFoundException.*;
import static com.portfolio.repository.util.MemberUtil.getAuthenticatedUsername;

@RequiredArgsConstructor
@Component
public class EditCommentValidator implements Validator {

    private final CommentRepository commentRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(EditComment.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EditComment request = (EditComment) target;
        if (request.getCommentId() != null) {
            Comment comment = commentRepository.findCommentWithMemberById(request.getCommentId());

            if (comment == null) {
                throw new CustomNotFoundException(COMMENT_NOT_FOUND);
            }
            if (comment.getMember().getUsername()
                    .equals(getAuthenticatedUsername()) == false) {
                throw new AuthorizationFailedException();
            }
        }
    }
}
