package plus.voyage.framework.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import plus.voyage.framework.dto.BoardCreateRequest
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
            ?: throw IllegalStateException("User not found: $username")
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
}
