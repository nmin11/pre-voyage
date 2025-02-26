package plus.voyage.framework.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import plus.voyage.framework.dto.SignupRequest
import plus.voyage.framework.entity.User
import plus.voyage.framework.repository.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun signup(request: SignupRequest) {
        val hashedPassword = passwordEncoder.encode(request.password)
        val user = User(
            username = request.username,
            password = hashedPassword
        )

        userRepository.save(user)
    }
}
