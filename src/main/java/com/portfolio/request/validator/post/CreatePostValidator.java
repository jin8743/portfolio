package com.portfolio.request.validator.post;

import com.portfolio.exception.custom.CustomNotFoundException;
import com.portfolio.repository.board.BoardRepository;
import com.portfolio.request.post.CreatePost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.portfolio.exception.custom.CustomNotFoundException.*;

@RequiredArgsConstructor
@Component
public class CreatePostValidator implements Validator {

    private final BoardRepository boardRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(CreatePost.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CreatePost request = (CreatePost) target;

        if (request.getBoardName() != null &&
                boardRepository.existsByBoardName(request.getBoardName()) == false) {

            throw new CustomNotFoundException(BOARD_NOT_FOUND);
        }
    }
}
