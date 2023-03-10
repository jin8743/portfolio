package com.portfolio.domain;

import com.portfolio.domain.editor.PostEditor;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE post SET is_enabled = false WHERE post_id=?")
@Where(clause = "is_enabled=true")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String title;

    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "post", cascade = PERSIST)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    private Boolean commentsAllowed = true;

    private Boolean isEnabled = true;

    @Builder
    public Post(String title, String content, Member member,
                Boolean commentsAllowed, Board board) {
        this.title = title;
        this.content = content;
        this.member = member;
        this.commentsAllowed = commentsAllowed;
        this.board = board;
    }

    public PostEditor.PostEditorBuilder toEditor() {
        return PostEditor.builder()
                .title(title)
                .content(content)
                .commentsAllowed(commentsAllowed);
    }

    /**
     * 여기에서만 글 제목, 내용, 댓글 허용 여부 수정 가능
     */
    public void edit(PostEditor postEditor) {
        this.title = postEditor.getTitle();
        this.content = postEditor.getContent();
        this.commentsAllowed = postEditor.getCommentsAllowed();
    }


    /**
     * 특정 글에 달려있는 삭제되지 않은 댓글수 조회
     */
    public static Integer loadCommentCount(Post post) {
        return (int) post.getComments().stream()
                .filter(c -> c.getIsEnabled() == true).count();
    }
}
