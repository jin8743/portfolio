package com.portfolio.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import java.util.HashMap;
import java.util.Map;

import static javax.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE board SET is_enabled = false WHERE board_id=?")
public class Board extends BaseEntity{

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Column(unique = true)
    private String boardName;

    @Column(unique = true)
    private String nickname;

    private Boolean isEnabled;

    @Builder
    public Board(String boardName, String nickname) {
        this.boardName = boardName;
        this.nickname = nickname;
    }

}
