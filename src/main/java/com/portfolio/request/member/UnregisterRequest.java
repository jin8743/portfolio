package com.portfolio.request.member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
@Getter
@NoArgsConstructor
public class UnregisterRequest {

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;

    @Builder
    public UnregisterRequest(String password) {
        this.password = password;
    }
}
