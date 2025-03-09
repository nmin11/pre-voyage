package plus.voyage.framework.dto

import plus.voyage.framework.entity.Role

data class UserRoleUpdateResponse(
    val userId: Int,
    val role: Role
)
