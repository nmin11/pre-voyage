package plus.voyage.framework.dto

import plus.voyage.framework.entity.Coffee

data class CoffeeListResponse(
    val totalCounts: Int,
    val coffeeList: List<Coffee>
)
