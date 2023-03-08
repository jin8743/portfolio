package com.portfolio.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
public class Board extends BaseEntity{

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(unique = true)
    private String boardName;

    //TODO 닉네임 설정
    @Column(unique = true)
    private String nickname;

    @Builder
    public Board(String boardName, String nickname) {
        this.boardName = boardName;
        this.nickname = nickname;
    }

}
