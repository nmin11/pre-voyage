package plus.voyage.framework.service

import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.springframework.security.access.AccessDeniedException
import plus.voyage.framework.entity.Board
import plus.voyage.framework.entity.Comment
import plus.voyage.framework.entity.Role
import plus.voyage.framework.entity.User
import plus.voyage.framework.exception.CommentNotFoundException
import plus.voyage.framework.repository.CommentRepository
import java.util.*
import kotlin.test.Test

class CommentServiceTest {
    private val commentRepository: CommentRepository = mockk()
    private val boardService: BoardService = mockk()
    private val userService: UserService = mockk()
    private val commentService = CommentService(
        commentRepository, boardService, userService
    )

    private lateinit var user: User
    private lateinit var admin: User
    private lateinit var board: Board
    private lateinit var comment: Comment

    @BeforeEach
    fun setup() {
        user = User(username = "testUser", password = "hashedPassword").apply { id = 1 }
        admin = User(username = "adminUser", password = "hashedPassword").apply { role = Role.ADMIN }
        board = Board(title = "Test Title", content = "Test Content", user = user).apply { id = 1 }
        comment = Comment(content = "Test Comment", board = board, user = user).apply { id = 1 }
    }

    @Test
    fun `댓글 작성 성공`() {
        // given
        every { userService.getCurrentUser() } returns user
        every { boardService.findById(board.id!!) } returns board
        every { commentRepository.save(any()) } returns comment

        // when
        val result = commentService.create(board.id!!, comment.content)

        // then
        assertEquals(comment.content, result.content)
        assertEquals("testUser", result.username)
        verify { commentRepository.save(any()) }
    }

    @Test
    fun `댓글 ID와 게시글 ID 불일치_댓글 수정 실패`() {
        // given
        every { commentRepository.findById(comment.id!!) } returns Optional.of(comment)

        // when & then
        assertThrows<IllegalArgumentException> {
            commentService.update(999, comment.id!!, "수정된 댓글")
        }
    }

    @Test
    fun `작성자가 아닌 사용자_댓글 수정 실패`() {
        // given
        every { commentRepository.findById(comment.id!!) } returns Optional.of(comment)
        every { userService.getCurrentUser() } returns admin

        // when & then
        assertThrows<AccessDeniedException> {
            commentService.update(board.id!!, comment.id!!, "수정된 댓글")
        }
    }

    @Test
    fun `댓글 수정 성공`() {
        // given
        every { commentRepository.findById(comment.id!!) } returns Optional.of(comment)
        every { userService.getCurrentUser() } returns user

        // when
        val updatedComment = commentService.update(board.id!!, comment.id!!, "수정된 댓글")

        // then
        assertEquals("수정된 댓글", updatedComment.content)
        verify { commentRepository.findById(comment.id!!) }
    }

    @Test
    fun `존재하지 않는 댓글_댓글 삭제 실패`() {
        // given
        every { commentRepository.findById(999) } returns Optional.empty()

        // when & then
        assertThrows<CommentNotFoundException> {
            commentService.delete(board.id!!, 999)
        }
    }

    @Test
    fun `작성자도 ADMIN도 아닌 사용자_댓글 삭제 실패`() {
        // given
        val otherUser = User(username = "otherUser", password = "hashedPassword").apply { id = 3 }
        every { commentRepository.findById(comment.id!!) } returns Optional.of(comment)
        every { userService.getCurrentUser() } returns otherUser

        // when & then
        assertThrows<AccessDeniedException> {
            commentService.delete(board.id!!, comment.id!!)
        }
    }

    @Test
    fun `댓글 작성자_댓글 삭제 성공`() {
        // given
        every { commentRepository.findById(comment.id!!) } returns Optional.of(comment)
        every { userService.getCurrentUser() } returns user
        every { commentRepository.deleteById(comment.id!!) } just Runs

        // when
        commentService.delete(board.id!!, comment.id!!)

        // then
        verify { commentRepository.deleteById(comment.id!!) }
    }

    @Test
    fun `ADMIN 사용자_댓글 삭제 성공`() {
        // given
        every { commentRepository.findById(comment.id!!) } returns Optional.of(comment)
        every { userService.getCurrentUser() } returns admin // ADMIN 권한
        every { commentRepository.deleteById(comment.id!!) } just Runs

        // when
        commentService.delete(board.id!!, comment.id!!)

        // then
        verify { commentRepository.deleteById(comment.id!!) }
    }
}
