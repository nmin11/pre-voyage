package plus.voyage.framework.exception

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import plus.voyage.framework.dto.SimpleMessageResponse

@Component
class JwtAccessDeniedHandler(
    private val objectMapper: ObjectMapper
) : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.status = HttpServletResponse.SC_FORBIDDEN
        val errorResponse = SimpleMessageResponse("권한이 없습니다.")
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}
