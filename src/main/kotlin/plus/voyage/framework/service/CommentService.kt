package plus.voyage.framework.service

import jakarta.transaction.Transactional
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import plus.voyage.framework.entity.Board
import plus.voyage.framework.entity.Comment
import plus.voyage.framework.entity.User
import plus.voyage.framework.repository.BoardRepository
import plus.voyage.framework.repository.CommentRepository
import plus.voyage.framework.repository.UserRepository

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val boardRepository: BoardRepository,
    private val userRepository: UserRepository
) {
    @Transactional
    fun create(boardId: Int, content: String) {
        val username = SecurityContextHolder.getContext().authentication.name
        val user: User = userRepository.findByUsername(username)
            ?: throw IllegalStateException("사용자 $username 을(를) 찾을 수 없습니다.")
        val board: Board = boardRepository.findById(boardId).orElseThrow {
            IllegalArgumentException("$boardId 번 게시글을 찾을 수 없습니다.")
        }

        val comment = Comment(content, board, user)

        commentRepository.save(comment)
    }
}
