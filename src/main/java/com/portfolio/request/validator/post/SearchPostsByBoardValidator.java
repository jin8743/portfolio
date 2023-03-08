package com.portfolio.request.validator.post;

import com.portfolio.exception.custom.CustomNotFoundException;
import com.portfolio.repository.board.BoardRepository;
import com.portfolio.request.post.SearchPostsByBoard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static com.portfolio.exception.custom.CustomNotFoundException.*;

@Component
@RequiredArgsConstructor
public class SearchPostsByBoardValidator implements Validator {

    private final BoardRepository boardRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SearchPostsByBoard.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SearchPostsByBoard request = (SearchPostsByBoard) target;
        if (boardRepository
                .existsByBoardName(request.getBoard()) == false) {
            throw new CustomNotFoundException(BOARD_NOT_FOUND);
        }
    }
}
