package plus.voyage.framework.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows
import org.springframework.security.access.AccessDeniedException
import plus.voyage.framework.dto.BoardItem
import plus.voyage.framework.dto.BoardRequest
import plus.voyage.framework.entity.Board
import plus.voyage.framework.entity.Role
import plus.voyage.framework.entity.User
import plus.voyage.framework.exception.BoardNotFoundException
import plus.voyage.framework.repository.BoardRepository
import java.util.*
import kotlin.test.Test

class BoardServiceTest {
    private val boardRepository: BoardRepository = mockk()
    private val userService: UserService = mockk()
    private val boardService = BoardService(
        boardRepository, userService
    )

    @Test
    fun `게시글 생성 성공`() {
        // given
        val currentUser = User(username = "testUser", password = "hashedPassword").apply { id = 1 }
        val request = BoardRequest(title = "New Post", content = "Content")
        val board = Board(request.title, request.content, currentUser).apply { id = 1 }

        every { userService.getCurrentUser() } returns currentUser
        every { boardRepository.save(any()) } returns board

        // when
        val response: BoardItem = boardService.create(request)

        // then
        assertEquals("New Post", response.title)
        assertEquals("testUser", response.username)
        verify(exactly = 1) { boardRepository.save(any()) }
    }

    @Test
    fun `존재하지 않는 게시글 ID 조회_게시글 단건 조회 실패`() {
        // given
        val mockUser = User(username = "testUser", password = "password123")
        every { userService.getCurrentUser() } returns mockUser // ✅ userService Mocking 추가
        every { boardRepository.findById(999) } returns Optional.empty()

        // when & then
        val exception = assertThrows<BoardNotFoundException> {
            boardService.getById(999)
        }

        assertEquals("999 번 게시글을 찾을 수 없습니다.", exception.message)
        verify { boardRepository.findById(999) }
    }

    @Test
    fun `게시글 단건 조회 성공`() {
        // given
        val currentUser = User(username = "testUser", password = "hashedPassword").apply { id = 1 }
        val board = Board("Title", "Content", currentUser).apply { id = 1 }

        every { userService.getCurrentUser() } returns currentUser
        every { boardRepository.findById(1) } returns Optional.of(board)

        // when
        val response: BoardItem = boardService.getById(1)

        // then
        assertEquals(1, response.boardId)
        assertEquals("Title", response.title)
        assertTrue(response.isAuthor!!) // 작성자 본인 확인
    }

    @Test
    fun `작성자가 아닌 사용자_게시글 수정 실패`() {
        // given
        val author = User(username = "authorUser", password = "hashedPassword").apply { id = 1 }
        val anotherUser = User(username = "otherUser", password = "hashedPassword").apply { id = 2 }
        val board = Board("Title", "Content", author).apply { id = 1 }
        val request = BoardRequest(title = "Updated Title", content = "Updated Content")

        every { userService.getCurrentUser() } returns anotherUser
        every { boardRepository.findById(1) } returns Optional.of(board)

        // when & then
        val exception = assertThrows<AccessDeniedException> {
            boardService.update(1, request)
        }

        assertEquals("게시글 수정 권한이 없습니다.", exception.message)
    }

    @Test
    fun `게시글 수정 성공`() {
        // given
        val currentUser = User(username = "testUser", password = "hashedPassword").apply { id = 1 }
        val board = Board("Old Title", "Old Content", currentUser).apply { id = 1 }
        val request = BoardRequest(title = "Updated Title", content = "Updated Content")

        every { userService.getCurrentUser() } returns currentUser
        every { boardRepository.findById(1) } returns Optional.of(board)

        // when
        val response: BoardItem = boardService.update(1, request)

        // then
        assertEquals("Updated Title", response.title)
        assertEquals("Updated Content", response.content)
        verify(exactly = 1) { boardRepository.findById(1) }
    }

    @Test
    fun `작성자도 ADMIN도 아닌 사용자_게시글 삭제 실패`() {
        // given
        val authorUser = User(username = "authorUser", password = "hashedPassword").apply { id = 1 }
        val anotherUser = User(username = "otherUser", password = "hashedPassword").apply { id = 2 }
        val board = Board("Title", "Content", authorUser).apply { id = 1 }

        every { userService.getCurrentUser() } returns anotherUser
        every { boardRepository.findById(1) } returns Optional.of(board)

        // when
        val exception = assertThrows<AccessDeniedException> {
            boardService.delete(1)
        }

        // then
        assertEquals("게시글 삭제 권한이 없습니다.", exception.message)
    }

    @Test
    fun `게시글 작성자_게시글 삭제 성공`() {
        // given
        val currentUser = User(username = "testUser", password = "hashedPassword").apply { id = 1 }
        val board = Board("Title", "Content", currentUser).apply { id = 1 }

        every { userService.getCurrentUser() } returns currentUser
        every { boardRepository.findById(1) } returns Optional.of(board)
        every { boardRepository.deleteById(1) } returns Unit

        // when
        boardService.delete(1)

        // then
        verify(exactly = 1) { boardRepository.deleteById(1) }
    }

    @Test
    fun `ADMIN 사용자_게시글 삭제 성공`() {
        // given
        val adminUser = User(username = "adminUser", password = "hashedPassword").apply { role = Role.ADMIN }
        val authorUser = User(username = "authorUser", password = "hashedPassword").apply { id = 1 }
        val board = Board("Title", "Content", authorUser).apply { id = 1 }

        every { userService.getCurrentUser() } returns adminUser
        every { boardRepository.findById(1) } returns Optional.of(board)
        every { boardRepository.deleteById(1) } returns Unit

        // when
        boardService.delete(1)

        // then
        verify(exactly = 1) { boardRepository.deleteById(1) }
    }
}
