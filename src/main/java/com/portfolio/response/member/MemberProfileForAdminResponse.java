package com.portfolio.response.member;

import com.portfolio.domain.Member;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
/** 관리자가 회원정보를 조회했을떄의 Response */
public class MemberProfileForAdminResponse {

    // 회원 번호
    private final Long id;

    //회원 아이디
    private final String username;

    //이메일
    private final String email;

    //탈퇴 여부
    //true -> 탈퇴하지 않은 상태
    private final Boolean isEnabled;

    //가입일자
    private final LocalDateTime createdAt;

    public MemberProfileForAdminResponse(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.email = member.getEmail();
        this.isEnabled = member.getIsEnabled();
        this.createdAt = member.getCreatedAt();
    }
}
