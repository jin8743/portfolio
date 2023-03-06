package com.portfolio.request.validator.board;

import com.portfolio.exception.custom.CustomBadRequestException;
import com.portfolio.repository.board.BoardRepository;
import com.portfolio.request.board.BoardCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


import static com.portfolio.exception.custom.CustomBadRequestException.*;

@RequiredArgsConstructor
@Component
public class BoardCreateValidator implements Validator {

    private final BoardRepository boardRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(BoardCreateRequest.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        BoardCreateRequest request = (BoardCreateRequest) target;
        if (boardRepository.existsByBoardName(request.getBoardName()) ||
                boardRepository.existsByNickname(request.getNickname())) {
            throw new CustomBadRequestException(DUPLICATED_BOARDNAME);
        }
    }
}
