package plus.voyage.framework.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import plus.voyage.framework.service.BoardService

@Controller
@RequestMapping("/boards")
class BoardController(
    private val boardService: BoardService
) {
    @GetMapping
    fun findAll(model: Model): String {
        val boardListResponse = boardService.findAll()
        model.addAttribute("boards", boardListResponse)
        return "boards/index"
    }
}
