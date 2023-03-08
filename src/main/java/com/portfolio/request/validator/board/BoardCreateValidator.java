package com.portfolio.request.validator.board;

import com.portfolio.exception.custom.CustomBadRequestException;
import com.portfolio.repository.board.BoardRepository;
import com.portfolio.request.board.CreateBoard;
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
        return clazz.isAssignableFrom(CreateBoard.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CreateBoard request = (CreateBoard) target;

        /** 이 두개 모두 null 이 아닐경우 검증 진행 */
        String boardName = request.getBoardName();
        String nickname = request.getNickname();

        if (boardName != null && nickname != null) {

            if (boardRepository.existsByBoardName(boardName)) {
                throw new CustomBadRequestException(DUPLICATED_BOARDNAME);
            }
            if (boardRepository.existsByNickname(nickname)) {
                throw new CustomBadRequestException(DUPLICATED_BOARD_NICKNAME);
            }
        }
    }
}
