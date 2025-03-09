package plus.voyage.framework.dto

import plus.voyage.framework.entity.Role
import plus.voyage.framework.entity.User

data class UserListResponse(
    val totalCounts: Int,
    val users: List<UserResponse>
)

data class UserResponse(
    val userId: Int,
    val username: String,
    val role: Role,
    val points: Int
) {
    companion object {
        fun from(user: User): UserResponse {
            return UserResponse(
                userId = user.id ?: throw IllegalStateException("존재하지 않는 사용자입니다."),
                username = user.username,
                role = user.role,
                points = user.points
            )
        }
    }
}
