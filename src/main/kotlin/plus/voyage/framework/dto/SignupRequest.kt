package plus.voyage.framework.dto

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class SignupRequest(
    @field:Size(
        min = 4,
        max = 10,
        message = "username 은 4자 이상 10자 이하여야 합니다."
    )
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*\\d)[a-z\\d]{4,10}$",
        message = "username 은 알파벳 소문자와 숫자를 포함해야 합니다."
    )
    val username: String = "",

    @field:Size(
        min = 8,
        max = 15,
        message = "password 는 8자 이상 15자 이하여야 합니다."
    )
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()_+\\-={}|\\[\\]:\";'<>?,./])[A-Za-z\\d!@#\$%^&*()_+\\-={}|\\[\\]:\";'<>?,./]{8,15}$",
        message = "password 는 알파벳 대소문자, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    val password: String = ""
)
