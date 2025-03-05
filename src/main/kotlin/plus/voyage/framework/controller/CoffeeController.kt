package plus.voyage.framework.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import plus.voyage.framework.dto.CoffeeCreateRequest
import plus.voyage.framework.entity.Coffee
import plus.voyage.framework.service.CoffeeService

@Controller
@RequestMapping("/coffee")
class CoffeeController(
    private val coffeeService: CoffeeService
) {
    @PostMapping
    fun create(@ModelAttribute request: CoffeeCreateRequest): String {
        coffeeService.create(request)
        return "redirect:/admin"
    }

    @GetMapping
    fun getAll(model: Model): String {
        val coffeeListResponse = coffeeService.getAll()
        model.addAttribute("coffeeList", coffeeListResponse)
        return "coffee/index"
    }

    @GetMapping("/weekly-popular")
    fun getWeeklyPopularCoffee(model: Model): String {
        val coffeeList = coffeeService.getWeeklyPopularCoffee()
        model.addAttribute("coffeeList", coffeeList)
        return "coffee/weekly-popular"
    }

    @PostMapping("/{id}/order")
    fun orderCoffee(@PathVariable id: Int): String {
        coffeeService.orderCoffee(id)
        return "redirect:/coffee"
    }
}
