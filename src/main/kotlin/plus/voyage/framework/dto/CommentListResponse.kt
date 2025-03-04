package plus.voyage.framework.dto

import java.time.LocalDateTime

data class CommentListResponse(
    val username: String,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
