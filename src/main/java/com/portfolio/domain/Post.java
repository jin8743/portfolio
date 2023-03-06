package com.portfolio.domain;

import com.portfolio.domain.editor.PostEditor;
import com.portfolio.exception.custom.CustomNotFoundException;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.portfolio.exception.custom.CustomNotFoundException.*;
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

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    private Boolean commentsAllowed = true;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board board;


    /** LikeService.like() 에서만 해당 값 변경 가능 */
    private Integer likes = 0;

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

    /** 여기에서만 글 제목, 내용 수정 가능*/
    public void edit(PostEditor postEditor) {
        this.title = postEditor.getTitle();
        this.content = postEditor.getContent();
        this.commentsAllowed = postEditor.getCommentsAllowed();
        System.out.println(this.commentsAllowed);
    }

    /**  LikeService.like() 에서만 해당 method 사용 가능 */
    public void increaseLike() {
        this.likes++;
    }

    /**  LikeService.like() 에서만 해당 method 사용 가능 */
    public void decreaseLike() {
        if (this.likes > 0) {
            this.likes--;
        }
    }

    public static void checkNull(Post post) {
        if (post == null) {
            throw new CustomNotFoundException(POST_NOT_FOUND);
        }
    }



}
