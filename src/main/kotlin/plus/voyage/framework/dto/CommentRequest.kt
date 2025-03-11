package plus.voyage.framework.dto

import jakarta.validation.constraints.NotBlank

data class CommentRequest(
    @field:NotBlank(message = "content 를 입력해주세요.")
    val content: String
)
