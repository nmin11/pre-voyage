package plus.voyage.framework.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import plus.voyage.framework.dto.SignupRequest
import plus.voyage.framework.service.UserService

@Controller
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {
    @PostMapping("/signup")
    fun signup(@ModelAttribute request: SignupRequest): String {
        userService.signup(request)
        return "redirect:/login"
    }
}
