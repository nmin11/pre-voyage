package plus.voyage.framework.dto

data class UserPointChargeResponse(
    val userId: Int,
    val username: String,
    val chargedPoints: Int,
    val totalPoints: Int
)
