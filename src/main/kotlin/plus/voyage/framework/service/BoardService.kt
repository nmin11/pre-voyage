package plus.voyage.framework.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import plus.voyage.framework.dto.BoardCreateRequest
import plus.voyage.framework.dto.BoardDetailResponse
import plus.voyage.framework.dto.BoardListResponse
import plus.voyage.framework.dto.BoardResponse
import plus.voyage.framework.entity.Board
import plus.voyage.framework.entity.User
import plus.voyage.framework.repository.BoardRepository
import plus.voyage.framework.repository.UserRepository

@Service
class BoardService(
    private val boardRepository: BoardRepository,
    private val userRepository: UserRepository
) {
    fun create(request: BoardCreateRequest) {
        val username = SecurityContextHolder.getContext().authentication.name
        val user: User = userRepository.findByUsername(username)
            ?: throw IllegalStateException("사용자 $username 을(를) 찾을 수 없습니다.")
        val board = Board(
            title = request.title,
            content = request.content,
            user
        )

        boardRepository.save(board)
    }

    fun getAll(): BoardListResponse {
        val boards = boardRepository.findAll()
            .map { BoardResponse.from(it) }

        return BoardListResponse(
            totalCounts = boards.size,
            boards
        )
    }

    fun getById(boardId: Int): BoardDetailResponse {
        val username = SecurityContextHolder.getContext().authentication.name
        val board: Board = boardRepository.findById(boardId).orElseThrow {
            throw NoSuchElementException("$boardId 번 게시글을 찾을 수 없습니다.")
        }
        val isAuthor = username == board.user.username

        return BoardDetailResponse(
            boardId,
            username = board.user.username,
            title = board.title,
            content = board.content,
            createdAt = board.createdAt,
            updatedAt = board.updatedAt,
            isAuthor
        )
    }
}
