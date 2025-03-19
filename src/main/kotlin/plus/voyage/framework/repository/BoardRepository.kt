package plus.voyage.framework.repository

import org.springframework.data.jpa.repository.JpaRepository
import plus.voyage.framework.entity.Board

interface BoardRepository : JpaRepository<Board, Int> {
    fun findAllByOrderByCreatedAtDesc(): List<Board>
}
