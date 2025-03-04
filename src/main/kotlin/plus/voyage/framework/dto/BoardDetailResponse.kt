package plus.voyage.framework.dto

import java.time.LocalDateTime

data class BoardDetailResponse(
    val boardId: Int,
    val username: String,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val isAuthor: Boolean,
    val comments: List<CommentListResponse>
)
