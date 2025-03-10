package plus.voyage.framework.controller

import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import plus.voyage.framework.dto.*
import plus.voyage.framework.service.BoardService
import plus.voyage.framework.service.CommentService

@Controller
@Profile("api")
@RequestMapping("/boards")
class BoardController(
    private val boardService: BoardService,
    private val commentService: CommentService
) {
    @PostMapping
    fun create(@RequestBody request: BoardCreateRequest): ResponseEntity<BoardCreateResponse> {
        val response = boardService.create(request)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response)
    }

    @GetMapping
    fun getAll(): ResponseEntity<BoardListResponse> {
        val response = boardService.getAll()
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Int): ResponseEntity<BoardItem> {
        val response = boardService.getById(id)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Int,
        @RequestBody request: BoardUpdateRequest
    ): ResponseEntity<BoardItem> {
        val response = boardService.update(id, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int): ResponseEntity<SimpleMessageResponse> {
        boardService.delete(id)
        return ResponseEntity.ok(
            SimpleMessageResponse("$id 번 게시글이 정상적으로 삭제되었습니다.")
        )
    }

    @PostMapping("/{id}/comments")
    fun createComment(
        @PathVariable id: Int,
        @RequestBody request: CommentCreateRequest
    ): ResponseEntity<CommentItem> {
        val response = commentService.create(id, request.content)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response)
    }

    @PutMapping("/{boardId}/comments/{commentId}")
    fun updateComment(
        @PathVariable boardId: Int,
        @PathVariable commentId: Int,
        content: String
    ): String {
        commentService.update(boardId, commentId, content)
        return "redirect:/boards/$boardId"
    }

    @DeleteMapping("/{boardId}/comments/{commentId}")
    fun deleteComment(
        @PathVariable boardId: Int,
        @PathVariable commentId: Int
    ): String {
        commentService.delete(commentId)
        return "redirect:/boards/$boardId"
    }
}
