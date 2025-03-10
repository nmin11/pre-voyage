package plus.voyage.framework.dto

data class BoardListResponse(
    val totalCounts: Int,
    val boards: List<BoardDetailResponse>
)
