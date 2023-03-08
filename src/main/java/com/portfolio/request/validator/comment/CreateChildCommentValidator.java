package com.portfolio.request.validator.comment;

import com.portfolio.domain.Comment;
import com.portfolio.exception.custom.CustomBadRequestException;
import com.portfolio.exception.custom.CustomNotFoundException;
import com.portfolio.repository.comment.CommentRepository;
import com.portfolio.request.comment.CreateChildComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.portfolio.exception.custom.CustomBadRequestException.*;
import static com.portfolio.exception.custom.CustomNotFoundException.*;

@Component
@RequiredArgsConstructor
public class CreateChildCommentValidator implements Validator {

    private final CommentRepository commentRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(CreateChildComment.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CreateChildComment request = (CreateChildComment) target;

        if (request.getParentCommentId() != null) {
            Comment comment = commentRepository.findCommentWithPostById(request.getParentCommentId());

            if (comment == null) {
                throw new CustomNotFoundException(COMMENT_NOT_FOUND);
            }
            if (comment.getPost().getCommentsAllowed() == false) {
                throw new CustomBadRequestException(COMMENTS_NOT_ALLOWED);
            }
        }
    }
}
