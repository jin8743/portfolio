package com.portfolio.request.member;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UnregisterRequest {

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;


    @Builder
    public UnregisterRequest(String password) {
        this.password = password;
    }
}
