package plus.voyage.framework.dto

data class CoffeeListResponse(
    val totalCounts: Int,
    val coffeeList: List<CoffeeItem>
)
