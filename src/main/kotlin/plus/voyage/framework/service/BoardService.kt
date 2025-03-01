package plus.voyage.framework.service

import org.springframework.stereotype.Service
import plus.voyage.framework.dto.BoardListResponse
import plus.voyage.framework.dto.BoardResponse
import plus.voyage.framework.repository.BoardRepository

@Service
class BoardService(
    private val boardRepository: BoardRepository
) {
    fun findAll(): BoardListResponse {
        val boards = boardRepository.findAll()
            .map { BoardResponse.from(it) }

        return BoardListResponse(
            totalCounts = boards.size,
            boards
        )
    }
}
