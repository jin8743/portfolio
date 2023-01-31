package com.portfolio.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Board extends BaseEntity{

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String boardName;

    private Boolean isEnabled;

    @Builder
    public Board(String boardName, Boolean isEnabled) {
        this.boardName = boardName;
        this.isEnabled = isEnabled;
    }
}
