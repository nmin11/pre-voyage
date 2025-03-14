package plus.voyage.framework.controller

import jakarta.validation.Valid
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import plus.voyage.framework.dto.*
import plus.voyage.framework.entity.Role
import plus.voyage.framework.exception.InvalidRoleException
import plus.voyage.framework.service.UserService

@Controller
@Profile("api")
@RequestMapping("/users")
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
    fun signup(@Valid @RequestBody request: SignupRequest): ResponseEntity<SignupResponse> {
        val response = userService.signup(request)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response)
    }

    @GetMapping
    fun getAll(): ResponseEntity<UserListResponse> {
        val response = userService.getAll()
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{id}/role")
    fun updateRole(
        @PathVariable id: Int,
        @RequestBody request: UserRoleUpdateRequest
    ): ResponseEntity<UserRoleUpdateResponse> {
        val newRole = try {
            Role.valueOf(request.role.uppercase())
        } catch (ex: IllegalArgumentException) {
            throw InvalidRoleException()
        }
        val response = userService.updateUserRole(id, newRole)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{id}/points")
    fun chargePoint(
        @PathVariable id: Int,
        @RequestBody request: UserPointChargeRequest
    ): ResponseEntity<UserPointChargeResponse> {
        val response = userService.chargePoint(id, request.points)
        return ResponseEntity.ok(response)
    }
}
