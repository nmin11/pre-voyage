package plus.voyage.framework.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import plus.voyage.framework.dto.SignupRequest

@Controller
class WebController {
    @GetMapping("/login")
    fun login(): String {
        return "login"
    }

    @GetMapping("/register")
    fun register(model: Model): String {
        model.addAttribute("registerForm", SignupRequest())
        return "register"
    }
}
