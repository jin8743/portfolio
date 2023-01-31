package com.portfolio.repository.board;

import com.portfolio.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {

    Optional<Board> findByBoardName(String boardName);
}
