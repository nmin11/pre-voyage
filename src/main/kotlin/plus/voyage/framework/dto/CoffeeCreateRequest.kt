package plus.voyage.framework.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero

data class CoffeeCreateRequest(
    @field:NotBlank(message = "커피 이름을 입력해주세요.")
    val name: String = "",

    @field:PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
    val price: Int = 0,

    @field:NotBlank(message = "이미지 URL 을 입력해주세요.")
    val imageUrl: String = ""
)
