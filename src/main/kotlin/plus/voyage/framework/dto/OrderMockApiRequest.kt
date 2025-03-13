package plus.voyage.framework.dto

data class OrderMockApiRequest(
    val orderId: String,
    val userId: Int,
    val coffeeId: Int,
    val price: Int
)
