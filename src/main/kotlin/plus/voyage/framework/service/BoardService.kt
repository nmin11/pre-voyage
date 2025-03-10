package plus.voyage.framework.service

import jakarta.transaction.Transactional
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import plus.voyage.framework.dto.*
import plus.voyage.framework.entity.Board
import plus.voyage.framework.entity.User
import plus.voyage.framework.repository.BoardRepository
import plus.voyage.framework.repository.UserRepository
import java.time.LocalDateTime

@Service
class BoardService(
    private val boardRepository: BoardRepository,
    private val userRepository: UserRepository
) {
    @Transactional
    fun create(request: BoardCreateRequest): BoardCreateResponse {
        val username = SecurityContextHolder.getContext().authentication.name
        val user: User = userRepository.findByUsername(username)
            ?: throw IllegalStateException("사용자 $username 을(를) 찾을 수 없습니다.")

        val board = boardRepository.save(
            Board(
                title = request.title,
                content = request.content,
                user
            )
        )

        return BoardCreateResponse(
            boardId = board.id ?: throw IllegalStateException("존재하지 않는 게시글입니다."),
            title = board.title,
            username,
            content = board.content,
            createdAt = board.createdAt,
            updatedAt = board.updatedAt
        )
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
        val comments = board.comments.map {
            CommentListResponse(
                commentId = it.id ?: throw IllegalStateException("존재하지 않는 댓글입니다."),
                username = it.user.username,
                content = it.content,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
                isAuthor = username == it.user.username
            )
        }

        return BoardDetailResponse(
            boardId,
            username = board.user.username,
            title = board.title,
            content = board.content,
            createdAt = board.createdAt,
            updatedAt = board.updatedAt,
            isAuthor = username == board.user.username,
            comments
        )
    }

    @Transactional
    fun update(boardId: Int, request: BoardUpdateRequest) {
        val username = SecurityContextHolder.getContext().authentication.name
        val board: Board = boardRepository.findById(boardId).orElseThrow {
            throw NoSuchElementException("$boardId 번 게시글을 찾을 수 없습니다.")
        }
        if (username != board.user.username) {
            throw AccessDeniedException("게시글 수정 권한이 없습니다.")
        }

        board.title = request.title
        board.content = request.content
        board.updatedAt = LocalDateTime.now()
    }

    @Transactional
    fun delete(boardId: Int) {
        boardRepository.deleteById(boardId)
    }
}
