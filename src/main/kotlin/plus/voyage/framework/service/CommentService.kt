package plus.voyage.framework.service

import jakarta.transaction.Transactional
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import plus.voyage.framework.dto.CommentItem
import plus.voyage.framework.entity.Comment
import plus.voyage.framework.entity.Role
import plus.voyage.framework.exception.CommentNotFoundException
import plus.voyage.framework.repository.CommentRepository
import java.time.LocalDateTime

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val boardService: BoardService,
    private val userService: UserService
) {
    @Transactional
    fun create(boardId: Int, content: String): CommentItem {
        val currentUser = userService.getCurrentUser()
        val board = boardService.findById(boardId)
        val comment = commentRepository.save(
            Comment(content, board, currentUser)
        )

        return CommentItem.from(comment, currentUser.username)
    }

    @Transactional
    fun update(boardId: Int, commentId: Int, content: String): CommentItem {
        val comment = findById(commentId)
        if (boardId != comment.board.id) {
            throw IllegalArgumentException("게시글과 댓글의 ID 가 일치하지 않습니다.")
        }
        val currentUser = userService.getCurrentUser()
        if (currentUser.username != comment.user.username) {
            throw AccessDeniedException("댓글 수정 권한이 없습니다.")
        }

        comment.content = content
        comment.updatedAt = LocalDateTime.now()

        return CommentItem.from(comment, currentUser.username)
    }

    @Transactional
    fun delete(boardId: Int, commentId: Int) {
        val comment = findById(commentId)
        if (boardId != comment.board.id) {
            throw IllegalArgumentException("게시글과 댓글의 ID 가 일치하지 않습니다.")
        }
        val currentUser = userService.getCurrentUser()
        if (currentUser.username != comment.user.username && currentUser.role != Role.ADMIN) {
            throw AccessDeniedException("댓글 삭제 권한이 없습니다.")
        }

        commentRepository.deleteById(commentId)
    }

    private fun findById(commentId: Int): Comment {
        return commentRepository.findById(commentId).orElseThrow {
            throw CommentNotFoundException("$commentId 번 댓글을 찾을 수 없습니다.")
        }
    }
}
