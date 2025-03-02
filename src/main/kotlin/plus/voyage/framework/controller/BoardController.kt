package plus.voyage.framework.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import plus.voyage.framework.dto.BoardCreateRequest
import plus.voyage.framework.service.BoardService

@Controller
@RequestMapping("/boards")
class BoardController(
    private val boardService: BoardService
) {
    @PostMapping
    fun create(@ModelAttribute request: BoardCreateRequest): String {
        boardService.create(request)
        return "redirect:/boards"
    }

    @GetMapping
    fun getAll(model: Model): String {
        val boardListResponse = boardService.getAll()
        model.addAttribute("boards", boardListResponse)
        return "boards/index"
    }
}
