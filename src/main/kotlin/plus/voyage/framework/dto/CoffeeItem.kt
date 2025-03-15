package plus.voyage.framework.dto

import plus.voyage.framework.entity.Coffee

data class CoffeeItem(
    val coffeeId: Int = 0,
    val name: String = "",
    val price: Int = 0,
    val imageUrl: String = ""
) {
    companion object {
        fun from(coffee: Coffee): CoffeeItem {
            return CoffeeItem(
                coffeeId = coffee.id ?: throw IllegalStateException("존재하지 않는 커피 메뉴입니다."),
                name = coffee.name,
                price = coffee.price,
                imageUrl = coffee.imageUrl
            )
        }
    }
}
