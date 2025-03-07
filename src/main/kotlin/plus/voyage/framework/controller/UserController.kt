package plus.voyage.framework.controller

import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import plus.voyage.framework.dto.LoginRequest
import plus.voyage.framework.dto.SignupRequest
import plus.voyage.framework.dto.LoginResponse
import plus.voyage.framework.entity.Role
import plus.voyage.framework.service.UserService

@Controller
@RequestMapping("/users")
@Profile("api")
class UserController(
    private val userService: UserService
) {
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        val response = userService.login(request)
        val headers = HttpHeaders().apply {
            set(HttpHeaders.AUTHORIZATION, "Bearer ${response.accessToken}")
        }

        return ResponseEntity
            .accepted()
            .headers(headers)
            .body(response)
    }

    @PostMapping("/signup")
    fun signup(@ModelAttribute request: SignupRequest): String {
        userService.signup(request)
        return "redirect:/login"
    }

    @PatchMapping("/{id}/role")
    fun updateRole(
        @PathVariable id: Int,
        role: String
    ): String {
        val userRole = Role.valueOf(role)
        userService.updateUserRole(id, userRole)
        return "redirect:/admin"
    }

    @PostMapping("/{id}/points")
    fun chargePoint(
        @PathVariable id: Int,
        points: Int
    ): String {
        userService.chargePoint(id, points)
        return "redirect:/admin"
    }
}
