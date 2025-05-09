package plus.voyage.framework.controller

import jakarta.validation.Valid
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import plus.voyage.framework.dto.CoffeeCreateRequest
import plus.voyage.framework.dto.CoffeeItem
import plus.voyage.framework.dto.CoffeeListResponse
import plus.voyage.framework.dto.CoffeeOrderResponse
import plus.voyage.framework.service.CoffeeService

@Controller
@Profile("api")
@RequestMapping("/coffee")
class CoffeeController(
    private val coffeeService: CoffeeService
) {
    @PostMapping
    fun create(@Valid @RequestBody request: CoffeeCreateRequest): ResponseEntity<CoffeeItem> {
        val response = coffeeService.create(request)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response)
    }

    @GetMapping
    fun getAll(): ResponseEntity<CoffeeListResponse> {
        val response = coffeeService.getAll()
        return ResponseEntity.ok(response)
    }

    @GetMapping("/weekly-popular")
    fun getWeeklyPopularCoffee(): ResponseEntity<CoffeeListResponse> {
        val response = coffeeService.getWeeklyPopularCoffee()
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{id}/order")
    fun orderCoffee(@PathVariable id: Int): ResponseEntity<CoffeeOrderResponse> {
        val response = coffeeService.orderCoffee(id)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response)
    }
}
