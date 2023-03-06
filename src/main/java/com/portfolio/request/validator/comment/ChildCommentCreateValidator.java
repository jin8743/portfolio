package com.portfolio.request.validator.comment;

import com.portfolio.domain.Comment;
import com.portfolio.exception.custom.CustomBadRequestException;
import com.portfolio.exception.custom.CustomNotFoundException;
import com.portfolio.repository.comment.CommentRepository;
import com.portfolio.request.comment.ParentCommentIdRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.portfolio.exception.custom.CustomBadRequestException.*;
import static com.portfolio.exception.custom.CustomNotFoundException.*;

@Component
@RequiredArgsConstructor
public class ChildCommentCreateValidator implements Validator {

    private final CommentRepository commentRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(ParentCommentIdRequest.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ParentCommentIdRequest request = (ParentCommentIdRequest) target;
        Comment comment = commentRepository.findCommentWithPostById(request.getCommentId());

        if (comment == null) {
            throw new CustomNotFoundException(COMMENT_NOT_FOUND);
        }
        if (comment.getPost().getCommentsAllowed() == false) {
            throw new CustomBadRequestException(COMMENTS_NOT_ALLOWED);
        }
    }
}
