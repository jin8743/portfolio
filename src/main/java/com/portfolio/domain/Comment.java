package com.portfolio.domain;

import com.portfolio.domain.util.CommentEditor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Comment extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Comment(Post post, String content, Member member) {
        //양방향 연관관계 설정
        this.post = post;
        post.getComments().add(this);

        this.content = content;
        this.member = member;
    }

    public CommentEditor.CommentEditorBuilder toEditor() {
        return CommentEditor.builder()
                .content(content);
    }

    /** 여기서만 데이터 수정 가능 */
    public void edit(CommentEditor commentEditor) {
        this.content = commentEditor.getContent();
    }


}
