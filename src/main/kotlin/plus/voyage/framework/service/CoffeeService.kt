package plus.voyage.framework.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import plus.voyage.framework.dto.CoffeeCreateRequest
import plus.voyage.framework.entity.Coffee
import plus.voyage.framework.repository.CoffeeRepository

@Service
class CoffeeService(
    private val coffeeRepository: CoffeeRepository
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
}
