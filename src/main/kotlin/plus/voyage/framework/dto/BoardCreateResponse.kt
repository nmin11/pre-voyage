package plus.voyage.framework.dto

import java.time.LocalDateTime

data class BoardCreateResponse(
    val boardId: Int,
    val title: String,
    val username: String,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
