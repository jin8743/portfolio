package com.portfolio.response.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.domain.Member;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.*;

@Getter
public class MyProfileResponse {

    private final String username;
    private final String email;
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    private final LocalDateTime joinedAt;

    public MyProfileResponse(Member member) {
        this.username = member.getUsername();
        this.email = member.getEmail();
        this.joinedAt = member.getCreatedAt();
    }
}
