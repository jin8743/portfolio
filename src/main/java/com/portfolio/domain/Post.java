package com.portfolio.domain;

import com.portfolio.domain.editor.PostEditor;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity{

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String title;
    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private  Member member;

    @OneToMany(mappedBy = "post", cascade = ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "liks", cascade = ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    private Boolean commentsAllowed = true;

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

    /** 여기에서만 글 제목, 내용, 댓글 허용 여부 수정 가능*/
    public void edit(PostEditor postEditor) {
        this.title = postEditor.getTitle();
        this.content = postEditor.getContent();
        this.commentsAllowed = postEditor.getCommentsAllowed();
    }

    // 글에 작성된 댓글과 대댓글의 총합
    public static Integer loadTotalComments(Post post) {
        AtomicInteger count = new AtomicInteger();
        post.getComments().forEach(comment -> count.addAndGet(comment.getChilds().size()));
        return count.get() + post.getComments().size();
    }
}
