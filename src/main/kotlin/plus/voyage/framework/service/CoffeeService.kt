package plus.voyage.framework.service

import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import plus.voyage.framework.dto.CoffeeCreateRequest
import plus.voyage.framework.dto.CoffeeItem
import plus.voyage.framework.dto.CoffeeListResponse
import plus.voyage.framework.entity.Coffee
import plus.voyage.framework.entity.Order
import plus.voyage.framework.entity.User
import plus.voyage.framework.repository.CoffeeRepository
import plus.voyage.framework.repository.OrderRepository
import plus.voyage.framework.repository.UserRepository
import java.time.LocalDateTime

@Service
class CoffeeService(
    private val coffeeRepository: CoffeeRepository,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository
) {
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

    fun getWeeklyPopularCoffee(): CoffeeListResponse {
        val targetDay = LocalDateTime.now().minusDays(7)
        val coffeeList = orderRepository.findPopularCoffeeSince(targetDay, PageRequest.of(0, 3))

        return CoffeeListResponse(
            totalCounts = coffeeList.size,
            coffeeList = coffeeList.map { CoffeeItem.from(it) }
        )
    }

    @Transactional
    fun orderCoffee(coffeeId: Int) {
        val username = SecurityContextHolder.getContext().authentication.name
        val user: User = userRepository.findByUsername(username)
            ?: throw IllegalStateException("사용자 $username 을(를) 찾을 수 없습니다.")
        val coffee: Coffee = coffeeRepository.findById(coffeeId).orElseThrow {
            IllegalArgumentException("$coffeeId 번 커피 메뉴를 찾을 수 없습니다.")
        }

        val order = Order(coffee, user)

        orderRepository.save(order)
    }
}
