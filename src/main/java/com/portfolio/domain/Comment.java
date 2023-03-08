package com.portfolio.domain;

import com.portfolio.domain.editor.CommentEditor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.*;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
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

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Comment> childs= new ArrayList<>();

    @Builder
    public Comment(Post post, Member member,
                   Comment parentComment, String content) {

        this.member = member;
        this.content = content;

        /** 생성하려는 댓글이 대댓글인 경우 양방향 연관 관계 설정 */
        if (parentComment != null) {
            this.parent = parentComment;
            parentComment.childs.add(this);
        }

        /** 생성하려는 댓글이 대댓글이 아닌경우 양방향 연관 관계 설정*/
        if (post != null) {
            this.post = post;
            post.getComments().add(this);
        }
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

//    public static Comment createChildComment(Member member, Comment parentComment, String content) {
//        Comment comment = Comment.builder()
//                .member(member)
//                .content(content)
//                .build();
//
//        //대댓글 연관관계 설정
//        parentComment.childs.add(comment);
//        comment.parent = parentComment;
//        return comment;
//    }


}
