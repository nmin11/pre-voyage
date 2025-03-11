package plus.voyage.framework.service

import jakarta.transaction.Transactional
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import plus.voyage.framework.dto.*
import plus.voyage.framework.entity.Role
import plus.voyage.framework.entity.User
import plus.voyage.framework.exception.DuplicateUsernameException
import plus.voyage.framework.exception.UserNotFoundException
import plus.voyage.framework.repository.UserRepository
import java.time.Instant

@Service
class UserService(
    private val userRepository: UserRepository,
    private val authenticationManager: AuthenticationManager,
    private val jwtEncoder: JwtEncoder,
    private val passwordEncoder: PasswordEncoder
) {
    fun signup(request: SignupRequest): SignupResponse {
        if (userRepository.existsByUsername(request.username)) {
            throw DuplicateUsernameException("중복된 username 입니다.")
        }
        val hashedPassword = passwordEncoder.encode(request.password)
        val user = userRepository.save(User(
            username = request.username,
            password = hashedPassword
        ))

        return SignupResponse(
            userId = user.id ?: throw IllegalStateException("존재하지 않는 사용자입니다."),
            username = user.username,
            role = user.role,
            points = user.points,
            message = "회원가입에 성공했습니다."
        )
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

    fun getAll(): UserListResponse {
        val users = userRepository.findAll()
            .map { UserItem.from(it) }

        return UserListResponse(
            totalCounts = users.size,
            users
        )
    }

    @Transactional
    fun updateUserRole(userId: Int, newRole: Role): UserRoleUpdateResponse {
        val user = findById(userId)
        user.role = newRole

        return UserRoleUpdateResponse(
            userId = userId,
            role = newRole
        )
    }

    @Transactional
    fun chargePoint(userId: Int, points: Int): UserPointChargeResponse {
        val user = findById(userId)
        user.points += points

        return UserPointChargeResponse(
            userId,
            username = user.username,
            chargedPoints = points,
            totalPoints = user.points
        )
    }

    fun getCurrentUser(): User {
        val username = SecurityContextHolder.getContext().authentication.name
        return userRepository.findByUsername(username)
            ?: throw BadCredentialsException("유효하지 않은 사용자 정보입니다.")
    }

    private fun findById(userId: Int): User {
        return userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("$userId 번 사용자를 찾을 수 없습니다.") }
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
