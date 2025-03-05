package plus.voyage.framework.service

import jakarta.transaction.Transactional
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import plus.voyage.framework.dto.CoffeeCreateRequest
import plus.voyage.framework.dto.CoffeeListResponse
import plus.voyage.framework.entity.Coffee
import plus.voyage.framework.entity.Order
import plus.voyage.framework.entity.User
import plus.voyage.framework.repository.CoffeeRepository
import plus.voyage.framework.repository.OrderRepository
import plus.voyage.framework.repository.UserRepository

@Service
class CoffeeService(
    private val coffeeRepository: CoffeeRepository,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository
) {
    @Transactional
    fun create(request: CoffeeCreateRequest) {
        val coffee = Coffee(
            name = request.name,
            price = request.price,
            imageUrl = request.imageUrl
        )

        coffeeRepository.save(coffee)
    }

    fun getAll(): CoffeeListResponse {
        val coffeeList = coffeeRepository.findAll()

        return CoffeeListResponse(
            totalCounts = coffeeList.size,
            coffeeList
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
