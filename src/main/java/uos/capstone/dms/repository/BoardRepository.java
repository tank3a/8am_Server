package uos.capstone.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uos.capstone.dms.domain.board.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
