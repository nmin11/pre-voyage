package plus.voyage.framework.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import plus.voyage.framework.dto.*
import plus.voyage.framework.entity.Coffee
import plus.voyage.framework.entity.Order
import plus.voyage.framework.exception.CoffeeNotFoundException
import plus.voyage.framework.exception.InsufficientPointsException
import plus.voyage.framework.exception.UserNotFoundException
import plus.voyage.framework.repository.CoffeeRepository
import plus.voyage.framework.repository.OrderRepository
import java.time.LocalDateTime

@Service
class CoffeeService(
    private val coffeeRepository: CoffeeRepository,
    private val orderRepository: OrderRepository,
    private val userService: UserService,
    private val objectMapper: ObjectMapper,
    private val restClient: RestClient
) {
    companion object {
        private const val MOCK_API_URL = "https://67d27eda90e0670699bdcabf.mockapi.io/Orders"
    }

    @Transactional
    fun create(request: CoffeeCreateRequest): CoffeeItem {
        val coffee = coffeeRepository.save(
            Coffee(
                name = request.name,
                price = request.price,
                imageUrl = request.imageUrl
            )
        )

        return CoffeeItem.from(coffee)
    }

    fun getAll(): CoffeeListResponse {
        val coffeeList = coffeeRepository.findAll()
            .map { CoffeeItem.from(it) }

        return CoffeeListResponse(
            totalCounts = coffeeList.size,
            coffeeList
        )
    }

    @Transactional(readOnly = true)
    @Cacheable(
        value = ["coffee_popular"],
        key = "'weekly'",
        unless = "#result != null and #result.totalCounts == 0"
    )
    fun getWeeklyPopularCoffee(): CoffeeListResponse {
        val targetDay = LocalDateTime.now().minusDays(7)
        val coffeeList = orderRepository.findPopularCoffeeSince(targetDay, PageRequest.of(0, 3))

        println("[DB Inquiry] New weekly popular coffee menus are calculated and saved")

        return CoffeeListResponse(
            totalCounts = coffeeList.size,
            coffeeList = coffeeList.map { CoffeeItem.from(it) }
        )
    }

    @Transactional
    @CacheEvict(value = ["coffee_popular"], key = "'weekly'")
    fun orderCoffee(coffeeId: Int): CoffeeOrderResponse {
        val coffee = coffeeRepository.findById(coffeeId).orElseThrow {
            CoffeeNotFoundException("$coffeeId 번 커피 메뉴를 찾을 수 없습니다.")
        }
        val currentUser = userService.getCurrentUser()
        if (currentUser.points - coffee.price < 0) {
            throw InsufficientPointsException()
        }

        currentUser.points -= coffee.price

        val order = orderRepository.save(
            Order(coffee, currentUser)
        )

        sendOrderToMockApi(order)

        return CoffeeOrderResponse.from(order)
    }

    @Async
    private fun sendOrderToMockApi(order: Order) {
        val request = OrderMockApiRequest(
            orderId = order.id.toString(),
            userId = order.user.id ?: throw UserNotFoundException("사용자를 찾을 수 없습니다."),
            coffeeId = order.coffee.id ?: throw CoffeeNotFoundException("커피 메뉴를 찾을 수 없습니다."),
            price = order.coffee.price
        )
        val jsonPayload = objectMapper.writeValueAsString(request)

        try {
            val response = restClient.post()
                .uri(MOCK_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonPayload)
                .retrieve()
                .toEntity(String::class.java)
            println("✅ Mock API Response: ${response.statusCode}, ${response.body}")
        } catch (ex: RestClientException) {
            println("❌ Mock API Request Failed: ${ex.message}")
        }
    }
}
