package plus.voyage.framework.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import plus.voyage.framework.dto.SimpleMessageResponse

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleArgumentValidationException(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String?>> {
        val errors = ex.bindingResult.fieldErrors.associate { it.field to it.defaultMessage }
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errors)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(ex: UserNotFoundException): ResponseEntity<SimpleMessageResponse> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(SimpleMessageResponse(ex.message))
    }

    @ExceptionHandler(DuplicateUsernameException::class)
    fun handleDuplicateUsername(ex: DuplicateUsernameException): ResponseEntity<Map<String, String?>> {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(mapOf("username" to ex.message))
    }

    @ExceptionHandler(InvalidRoleException::class)
    fun handleInvalidRoleException(ex: InvalidRoleException): ResponseEntity<Map<String, String?>> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(mapOf("role" to ex.message))
    }
}
