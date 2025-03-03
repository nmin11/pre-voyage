package plus.voyage.framework.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import plus.voyage.framework.dto.BoardCreateRequest
import plus.voyage.framework.dto.BoardUpdateRequest
import plus.voyage.framework.dto.SignupRequest
import plus.voyage.framework.service.BoardService
import plus.voyage.framework.service.UserService

@Controller
class WebController(
    private val userService: UserService,
    private val boardService: BoardService
) {
    @GetMapping("/login")
    fun login(): String {
        return "login"
    }

    @GetMapping("/register")
    fun register(model: Model): String {
        model.addAttribute("registerForm", SignupRequest())
        return "register"
    }

    @GetMapping("/boards/create")
    fun createBoard(model: Model): String {
        model.addAttribute("boardForm", BoardCreateRequest())
        return "boards/create"
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
}
