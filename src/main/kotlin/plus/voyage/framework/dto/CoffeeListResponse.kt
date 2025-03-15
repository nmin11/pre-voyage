package plus.voyage.framework.dto

data class CoffeeListResponse(
    val totalCounts: Int = 0,
    val coffeeList: List<CoffeeItem> = mutableListOf()
)
