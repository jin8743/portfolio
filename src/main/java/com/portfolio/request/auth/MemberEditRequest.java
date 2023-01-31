package com.portfolio.request.auth;

import lombok.Builder;
import lombok.Data;

@Data
public class MemberEditRequest {

    private String password;

    @Builder
    public MemberEditRequest(String password) {
        this.password = password;
    }
}
