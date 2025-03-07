package plus.voyage.framework.dto

import plus.voyage.framework.entity.Role

data class SignupResponse(
    val userId: Int,
    val username: String,
    val role: Role,
    val points: Int,
    val message: String
)
