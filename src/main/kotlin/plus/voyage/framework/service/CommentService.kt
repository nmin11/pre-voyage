package plus.voyage.framework.service

import jakarta.transaction.Transactional
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import plus.voyage.framework.dto.CommentItem
import plus.voyage.framework.entity.Board
import plus.voyage.framework.entity.Comment
import plus.voyage.framework.entity.User
import plus.voyage.framework.repository.BoardRepository
import plus.voyage.framework.repository.CommentRepository
import plus.voyage.framework.repository.UserRepository
import java.time.LocalDateTime

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val boardRepository: BoardRepository,
    private val userRepository: UserRepository
) {
    @Transactional
    fun create(boardId: Int, content: String): CommentItem {
        val username = SecurityContextHolder.getContext().authentication.name
        val user: User = userRepository.findByUsername(username)
            ?: throw IllegalStateException("사용자 $username 을(를) 찾을 수 없습니다.")
        val board: Board = boardRepository.findById(boardId).orElseThrow {
            IllegalArgumentException("$boardId 번 게시글을 찾을 수 없습니다.")
        }
        val comment = commentRepository.save(
            Comment(content, board, user)
        )

        return CommentItem.from(comment, username)
    }

    @Transactional
    fun update(boardId: Int, commentId: Int, content: String) {
        val username = SecurityContextHolder.getContext().authentication.name
        val comment: Comment = commentRepository.findById(commentId).orElseThrow {
            throw NoSuchElementException("$commentId 번 댓글을 찾을 수 없습니다.")
        }
        if (username != comment.user.username) {
            throw AccessDeniedException("댓글 수정 권한이 없습니다.")
        }
        val board: Board = boardRepository.findById(boardId).orElseThrow {
            throw NoSuchElementException("$boardId 번 게시글을 찾을 수 없습니다.")
        }
        if (comment.board != board) {
            throw IllegalArgumentException("게시글과 댓글의 ID 가 일치하지 않습니다.")
        }

        comment.content = content
        comment.updatedAt = LocalDateTime.now()
    }

    @Transactional
    fun delete(commentId: Int) {
        commentRepository.deleteById(commentId)
    }
}
