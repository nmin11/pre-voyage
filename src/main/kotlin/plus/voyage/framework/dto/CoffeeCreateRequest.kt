package plus.voyage.framework.dto

data class CoffeeCreateRequest(
    val name: String = "",
    val price: Int = 0,
    val imageUrl: String = ""
)
