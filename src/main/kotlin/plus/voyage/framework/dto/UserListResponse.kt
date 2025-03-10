package plus.voyage.framework.dto

data class UserListResponse(
    val totalCounts: Int,
    val users: List<UserItem>
)
