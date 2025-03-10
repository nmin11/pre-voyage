package plus.voyage.framework.controller

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import plus.voyage.framework.dto.BoardCreateRequest
import plus.voyage.framework.dto.BoardUpdateRequest
import plus.voyage.framework.dto.CoffeeCreateRequest
import plus.voyage.framework.dto.SignupRequest
import plus.voyage.framework.entity.Role
import plus.voyage.framework.service.BoardService
import plus.voyage.framework.service.UserService

@Controller
@Profile("thymeleaf")
class WebController(
    private val userService: UserService,
    private val boardService: BoardService
) {
    @GetMapping("/login")
    fun getLoginPage(): String {
        return "login"
    }

    @GetMapping("/register")
    fun getRegisterPage(model: Model): String {
        model.addAttribute("registerForm", SignupRequest())
        return "register"
    }

    @PostMapping("/users/signup")
    fun signup(@ModelAttribute request: SignupRequest): String {
        userService.signup(request)
        return "redirect:/login"
    }

    @PatchMapping("/users/{id}/role")
    fun updateUserRole(
        @PathVariable id: Int,
        role: String
    ): String {
        val userRole = Role.valueOf(role)
        userService.updateUserRole(id, userRole)
        return "redirect:/admin"
    }

    @PostMapping("/users/{id}/points")
    fun chargeUserPoint(
        @PathVariable id: Int,
        points: Int
    ): String {
        userService.chargePoint(id, points)
        return "redirect:/admin"
    }

    @GetMapping("/boards/create")
    fun getCreateBoardPage(model: Model): String {
        model.addAttribute("boardForm", BoardCreateRequest())
        return "boards/create"
    }

    @PostMapping("/boards")
    fun createBoard(@ModelAttribute request: BoardCreateRequest): String {
        boardService.create(request)
        return "redirect:/boards"
    }

    @GetMapping("/boards")
    fun getAllBoard(model: Model): String {
        val boardListResponse = boardService.getAll()
        model.addAttribute("boards", boardListResponse)
        return "boards/index"
    }

    @GetMapping("/boards/update/{id}")
    fun updateBoard(
        @PathVariable id: Int,
        model: Model
    ): String {
        val board = boardService.getById(id)
        model.addAttribute("boardId", id)
        model.addAttribute(
            "boardForm",
            BoardUpdateRequest(board.title, board.content)
        )
        return "boards/update"
    }

    @GetMapping("/admin")
    fun adminPageWithUserList(model: Model): String {
        val users = userService.getAll()
        model.addAttribute("users", users)
        return "admin/index"
    }

    @GetMapping("/admin/coffee/new")
    fun adminNewCoffeeMenuPage(model: Model): String {
        model.addAttribute("newCoffeeForm", CoffeeCreateRequest())
        return "admin/coffee/new"
    }
}
