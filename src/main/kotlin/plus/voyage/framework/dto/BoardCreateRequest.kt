package plus.voyage.framework.dto

import jakarta.validation.constraints.NotBlank

data class BoardCreateRequest(
    @field:NotBlank(message = "title 을 입력해주세요.")
    val title: String = "",

    @field:NotBlank(message = "content 를 입력해주세요.")
    val content: String = ""
)
