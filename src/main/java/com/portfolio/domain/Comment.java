package com.portfolio.domain;

import com.portfolio.domain.editor.CommentEditor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.*;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE comment SET is_enabled = false WHERE comment_id=?")
public class Comment extends BaseEntity{

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent")
    private List<Comment> childs= new ArrayList<>();

    private Boolean isEnabled = true;

    @Builder
    public Comment(Post post, Member member,
                   Comment parentComment, String content) {

        this.member = member;
        this.content = content;

        //양방향 연관관계 설정
        bindPostAndComment(post);

        //양방향 연관관계 설정
        bindParentAndChildComment(parentComment);
    }

    private void bindParentAndChildComment(Comment parentComment) {
        this.parent = parentComment;
        if (parentComment != null) {
            parentComment.getChilds().add(this);
        }
    }

    private void bindPostAndComment(Post post) {
        this.post = post;
        post.getComments().add(this);
    }

    public CommentEditor.CommentEditorBuilder toEditor() {
        return CommentEditor.builder()
                .content(content);
    }

    /** 여기서만 데이터 수정 가능 */
    public void edit(CommentEditor commentEditor) {
        this.content = commentEditor.getContent();
    }


    public static Comment createComment(Post post, Member member,
                                        Comment parentComment, String content) {
        return Comment.builder()
                .post(post)
                .member(member)
                .parentComment(parentComment)
                .content(content)
                .build();
    }
}
