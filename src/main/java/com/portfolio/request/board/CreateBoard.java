package com.portfolio.request.board;

import com.portfolio.domain.Board;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
public class CreateBoard {

    @NotBlank(message = "게시판 이름을 입력해주세요")
    @Size(max = 10, message = "10글자 이하, 알파벳만 가능합니다")
    @Pattern(regexp = "^[a-z]+$")
    private String boardName;

    @NotBlank(message = "게시판 별칭을 입력해주세요")
    @Size(max = 10, message = "10글자 이하, 한글과 공백만 가능합니다")
    @Pattern(regexp = "^[ㄱ-ㅎㅏ-ㅣ가-힣\\\\s]+$")
    private String nickname;

    @Builder
    public CreateBoard(String boardName, String nickname) {
        this.boardName = boardName;
        this.nickname = nickname;
    }

    public static Board createNewBoard(CreateBoard request) {
        return Board.builder()
                .boardName(request.getBoardName())
                .nickname(request.getNickname())
                .build();
    }
}
