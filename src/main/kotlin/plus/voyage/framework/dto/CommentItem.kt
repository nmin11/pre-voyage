package plus.voyage.framework.dto

import plus.voyage.framework.entity.Comment
import java.time.LocalDateTime

data class CommentItem(
    val commentId: Int,
    val boardId: Int,
    val username: String,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val isAuthor: Boolean?
) {
    companion object {
        fun from(comment: Comment, username: String): CommentItem {
            return CommentItem(
                commentId = comment.id ?: throw IllegalStateException("존재하지 않는 댓글입니다."),
                boardId = comment.board.id ?: throw IllegalStateException("존재하지 않는 게시글입니다."),
                username = comment.user.username,
                content = comment.content,
                createdAt = comment.createdAt,
                updatedAt = comment.updatedAt,
                isAuthor = username == comment.user.username
            )
        }
    }
}
