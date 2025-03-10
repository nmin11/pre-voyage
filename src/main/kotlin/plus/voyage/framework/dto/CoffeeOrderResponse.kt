package plus.voyage.framework.dto

import plus.voyage.framework.entity.Order
import java.time.LocalDateTime

data class CoffeeOrderResponse(
    val orderId: Long,
    val coffeeId: Int,
    val orderDate: LocalDateTime
) {
    companion object {
        fun from(order: Order): CoffeeOrderResponse {
            return CoffeeOrderResponse(
                orderId = order.id ?: throw IllegalStateException("존재하지 않는 주문입니다."),
                coffeeId = order.coffee.id ?: throw IllegalStateException("존재하지 않는 커피 메뉴입니다."),
                orderDate = order.createdAt
            )
        }
    }
}
