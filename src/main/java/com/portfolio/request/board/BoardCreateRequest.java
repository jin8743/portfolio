package com.portfolio.request.board;

import com.portfolio.domain.Board;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class BoardCreateRequest {

    @NotBlank(message = "게시판 이름은 공백일수 없습니다")
    @Size(max = 10, message = "게시판 이름은 10글자 이하로 정해주세요")
    private String boardName;

    @NotNull(message = "게시판 공개여부를 선택해주세요")
    private Boolean isEnabled;

    public static Board toEntity(BoardCreateRequest request) {

        return Board.builder()
                .boardName(request.getBoardName())
                .build();
    }
}
