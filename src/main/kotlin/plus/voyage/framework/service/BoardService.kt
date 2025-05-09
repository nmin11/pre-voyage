package plus.voyage.framework.service

import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
        val currentUser = userService.getCurrentUser()
        val boards = boardRepository.findAllByOrderByCreatedAtDesc()
            .map { BoardItem.from(it, currentUser.username) }

        return BoardListResponse(
            totalCounts = boards.size,
            boards
        )
    }

    fun getById(boardId: Int): BoardItem {
        val currentUser = userService.getCurrentUser()
        val board = findById(boardId)

        return BoardItem.from(board, currentUser.username)
    }

    @Transactional
    fun update(boardId: Int, request: BoardRequest): BoardItem {
        val board = findById(boardId)
        val currentUser = userService.getCurrentUser()
        if (currentUser.username != board.user.username) {
            throw AccessDeniedException("게시글 수정 권한이 없습니다.")
        }

        board.title = request.title
        board.content = request.content
        board.updatedAt = LocalDateTime.now()

        return BoardItem.from(board, currentUser.username)
    }

    @Transactional
    fun delete(boardId: Int) {
        val board = findById(boardId)
        val currentUser = userService.getCurrentUser()
        if (currentUser.username != board.user.username && currentUser.role != Role.ADMIN) {
            throw AccessDeniedException("게시글 삭제 권한이 없습니다.")
        }

        boardRepository.deleteById(boardId)
    }

    fun findById(boardId: Int): Board {
        return boardRepository.findById(boardId).orElseThrow {
            throw BoardNotFoundException("$boardId 번 게시글을 찾을 수 없습니다.")
        }
    }
}
