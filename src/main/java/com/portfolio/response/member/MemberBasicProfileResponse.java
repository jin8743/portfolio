package com.portfolio.response.member;

import com.portfolio.domain.Member;
import com.portfolio.repository.util.MemberUtil;
import lombok.Builder;
import lombok.Getter;

import static com.portfolio.repository.util.MemberUtil.*;
import static com.portfolio.repository.util.MemberUtil.getAuthenticatedUsername;

@Getter
public class MemberBasicProfileResponse {

    //회원이름 (탈퇴한 회원인 경우 "탈퇴한 회원" 이라고 표시됨)
    private final String username;

    //탈퇴한 회원인지 여부
    private final Boolean isEnabled;

    //총 작성한 글 수
    private final Long totalPosts;

    //총 작성한 댓글 수
    private final Long totalComments;

    //현재 접속하고 있는 회원의 대한 정보인지 여부
    private final Boolean myProfile;

    // 해당 회원이 관리자인지 여부
    private final Boolean isAdmin;

    @Builder
    public MemberBasicProfileResponse(Member member, Long totalPosts, Long totalComments) {
        this.username = getUsername(member);
        this.isEnabled = member.getIsEnabled();
        this.totalPosts = totalPosts;
        this.totalComments = totalComments;
        this.myProfile = member.getUsername().equals(getAuthenticatedUsername());
        this.isAdmin = isAdmin();
    }

    private static String getUsername(Member member) {
        return member.getIsEnabled() == true ? member.getUsername() : "탈퇴한 회원";
    }
}
