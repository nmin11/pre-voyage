package plus.voyage.framework.dto

import plus.voyage.framework.entity.Board
import java.time.LocalDateTime

data class BoardItem(
    val boardId: Int,
    val username: String,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val isAuthor: Boolean?,
    val comments: List<CommentItem>
) {
    companion object {
        fun from(board: Board, username: String): BoardItem {
            return BoardItem(
                boardId = board.id ?: throw IllegalStateException("존재하지 않는 게시글입니다."),
                username = board.user.username,
                title = board.title,
                content = board.content,
                createdAt = board.createdAt,
                updatedAt = board.updatedAt,
                isAuthor = username == board.user.username,
                comments = board.comments
                    .sortedByDescending { it.createdAt }
                    .map { CommentItem.from(it, username) }
            )
        }
    }
}
