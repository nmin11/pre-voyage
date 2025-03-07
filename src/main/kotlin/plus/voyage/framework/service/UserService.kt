package plus.voyage.framework.service

import jakarta.transaction.Transactional
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import plus.voyage.framework.dto.LoginRequest
import plus.voyage.framework.dto.SignupRequest
import plus.voyage.framework.dto.LoginResponse
import plus.voyage.framework.entity.Role
import plus.voyage.framework.entity.User
import plus.voyage.framework.repository.UserRepository
import java.time.Instant

@Service
class UserService(
    private val userRepository: UserRepository,
    private val authenticationManager: AuthenticationManager,
    private val jwtEncoder: JwtEncoder,
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

    fun login(request: LoginRequest): LoginResponse {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.username, request.password)
        )
        val user = authentication.principal as UserDetails
        val accessToken = generateToken(user)

        return LoginResponse(
            accessToken = accessToken,
            message = "로그인에 성공했습니다."
        )
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

    private fun generateToken(user: UserDetails): String {
        val now = Instant.now()
        val claims = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(3600))
            .subject(user.username)
            .claim("roles", user.authorities.map { it.authority })
            .build()

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue
    }
}
