package plus.voyage.framework.exception

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import plus.voyage.framework.dto.SimpleMessageResponse

@Component
class JwtAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        val errorResponse = SimpleMessageResponse("유효하지 않은 사용자 정보입니다.")
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}
