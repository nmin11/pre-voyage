package plus.voyage.framework.service

import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import plus.voyage.framework.dto.SignupRequest
import plus.voyage.framework.entity.Role
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

    fun getAll(): List<User> {
        return userRepository.findAll()
    }

    @Transactional
    fun updateUserRole(userId: Int, newRole: Role) {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("$userId 번 사용자를 찾을 수 없습니다.") }
        user.role = newRole
    }

    @Transactional
    fun chargePoint(userId: Int, points: Int) {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("$userId 번 사용자를 찾을 수 없습니다.") }
        user.points += points
    }
}
