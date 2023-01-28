package com.portfolio.request;

import com.portfolio.domain.Member;
import com.portfolio.domain.Post;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class PostCreateRequest {

    @NotBlank(message = "제목을 입력해주세요")
    @Size(max = 30, message = "제목은 30글자 이하로 작성해주세요")
    private String title;

    @NotBlank(message = "내용을 입력해주세요")
    private String content;


    @Builder
    public PostCreateRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public static Post toEntity(PostCreateRequest postCreate, Member member) {

        return Post.builder()
                .title(postCreate.getTitle())
                .content(postCreate.getContent())
                .member(member)
                .build();
    }
}
