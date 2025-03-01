package plus.voyage.framework.dto

import plus.voyage.framework.entity.Board
import java.time.LocalDateTime

data class BoardListResponse(
    val totalCounts: Int,
    val boards: List<BoardResponse>
)

data class BoardResponse(
    val boardId: Int,
    val username: String,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(board: Board): BoardResponse {
            return BoardResponse(
                boardId = board.id ?: throw IllegalStateException("존재하지 않는 게시글입니다."),
                username = board.user.username,
                title = board.title,
                content = board.content,
                createdAt = board.createdAt,
                updatedAt = board.updatedAt
            )
        }
    }
}
