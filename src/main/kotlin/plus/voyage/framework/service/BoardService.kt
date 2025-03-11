package plus.voyage.framework.service

import jakarta.transaction.Transactional
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import plus.voyage.framework.dto.*
import plus.voyage.framework.entity.Board
import plus.voyage.framework.entity.Role
import plus.voyage.framework.exception.BoardNotFoundException
import plus.voyage.framework.repository.BoardRepository
import java.time.LocalDateTime

@Service
class BoardService(
    private val boardRepository: BoardRepository,
    private val userService: UserService
) {
    @Transactional
    fun create(request: BoardRequest): BoardItem {
        val currentUser = userService.getCurrentUser()
        val board = boardRepository.save(
            Board(
                title = request.title,
                content = request.content,
                currentUser
            )
        )

        return BoardItem.from(board, currentUser.username)
    }

    fun getAll(): BoardListResponse {
        val username = SecurityContextHolder.getContext().authentication.name
        val boards = boardRepository.findAll()
            .map { BoardItem.from(it, username) }

        return BoardListResponse(
            totalCounts = boards.size,
            boards
        )
    }

    fun getById(boardId: Int): BoardItem {
        val username = SecurityContextHolder.getContext().authentication.name
        val board: Board = boardRepository.findById(boardId).orElseThrow {
            throw BoardNotFoundException("$boardId 번 게시글을 찾을 수 없습니다.")
        }

        return BoardItem.from(board, username)
    }

    @Transactional
    fun update(boardId: Int, request: BoardRequest): BoardItem {
        val board: Board = boardRepository.findById(boardId).orElseThrow {
            throw BoardNotFoundException("$boardId 번 게시글을 찾을 수 없습니다.")
        }
        val currentUser = userService.getCurrentUser()
        if (currentUser.username != board.user.username && currentUser.role != Role.ADMIN) {
            throw AccessDeniedException("게시글 수정 권한이 없습니다.")
        }

        board.title = request.title
        board.content = request.content
        board.updatedAt = LocalDateTime.now()

        return BoardItem.from(board, currentUser.username)
    }

    @Transactional
    fun delete(boardId: Int) {
        boardRepository.deleteById(boardId)
    }
}
