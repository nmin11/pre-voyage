package plus.voyage.framework.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import plus.voyage.framework.auth.JwtUtil
import plus.voyage.framework.auth.PrincipalDetails
import plus.voyage.framework.dto.LoginRequest
import plus.voyage.framework.dto.SignupRequest
import plus.voyage.framework.dto.UserPointChargeResponse
import plus.voyage.framework.entity.Role
import plus.voyage.framework.entity.User
import plus.voyage.framework.exception.DuplicateUsernameException
import plus.voyage.framework.exception.UserNotFoundException
import plus.voyage.framework.repository.UserRepository

class UserServiceTest {
    private val userRepository: UserRepository = mockk()
    private val authenticationManager: AuthenticationManager = mockk()
    private val jwtUtil: JwtUtil = mockk()
    private val passwordEncoder: PasswordEncoder = mockk()
    private val userService = UserService(
        userRepository, authenticationManager, jwtUtil, passwordEncoder
    )

    @Test
    fun `이미 등록된 username_회원가입 실패`() {
        // given
        val request = SignupRequest(username = "existingUser", password = "password123")
        every { userRepository.existsByUsername(request.username) } returns true

        // when
        assertThrows<DuplicateUsernameException> {
            userService.signup(request)
        }

        // then
        verify(exactly = 1) { userRepository.existsByUsername(request.username) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `회원가입 성공`() {
        // given
        val request = SignupRequest(username = "newUser", password = "ValidPass1!")
        val hashedPassword = "hashedPassword"

        every { userRepository.existsByUsername(request.username) } returns false
        every { passwordEncoder.encode(request.password) } returns hashedPassword
        every { userRepository.save(any()) } answers {
            val savedUser = firstArg<User>()
            savedUser.apply { id = 1 }
        }

        // when
        val response = userService.signup(request)

        // then
        assertThat(response.userId).isEqualTo(1)
        assertThat(response.username).isEqualTo(request.username)
        assertThat(response.message).isEqualTo("회원가입에 성공했습니다.")

        verify(exactly = 1) { userRepository.existsByUsername(request.username) }
        verify(exactly = 1) { passwordEncoder.encode(request.password) }
        verify(exactly = 1) { userRepository.save(any()) }
    }

    @Test
    fun `잘못된 username 또는 password_로그인 실패`() {
        // given
        val request = LoginRequest(username = "wrongUser", password = "wrongPassword")
        every {
            authenticationManager.authenticate(any())
        } throws BadCredentialsException("잘못된 인증 정보입니다.") // 인증 실패 시 예외 발생

        // when
        val exception = assertThrows<BadCredentialsException> {
            userService.login(request)
        }

        // then
        assertThat(exception.message).isEqualTo("잘못된 인증 정보입니다.")
        verify(exactly = 1) { authenticationManager.authenticate(any()) }
    }

    @Test
    fun `로그인 성공`() {
        // given
        val request = LoginRequest(username = "testUser", password = "password123")
        val user = User(username = request.username, password = "encodedPassword")
        val principalDetails = PrincipalDetails(user)
        val authentication = mockk<Authentication>()

        every { authentication.principal } returns principalDetails
        every {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.username, request.password)
            )
        } returns authentication
        every { jwtUtil.generateToken(any(), any()) } returns "mocked-token"

        // when
        val response = userService.login(request)

        // then
        assertEquals("mocked-token", response.accessToken)
        assertEquals("로그인에 성공했습니다.", response.message)
    }

    @Test
    fun `존재하지 않는 userId_역할 변경 실패`() {
        // given
        val invalidUserId = 999
        every { userRepository.findById(invalidUserId) } returns java.util.Optional.empty()

        // when
        val exception = assertThrows<UserNotFoundException> {
            userService.updateUserRole(invalidUserId, Role.ADMIN)
        }

        // then
        assertEquals("$invalidUserId 번 사용자를 찾을 수 없습니다.", exception.message)
        verify(exactly = 1) { userRepository.findById(invalidUserId) }
    }

    @Test
    fun `역할 변경 성공`() {
        // given
        val userId = 1
        val user = User(username = "testUser", password = "password123")
        every { userRepository.findById(userId) } returns java.util.Optional.of(user)

        // when
        val response = userService.updateUserRole(userId, Role.ADMIN)

        // then
        assertEquals(userId, response.userId)
        assertEquals(Role.ADMIN, response.role)
        assertEquals(Role.ADMIN, user.role) // 실제 객체의 role도 변경되었는지 확인
        verify(exactly = 1) { userRepository.findById(userId) }
    }

    @Test
    fun `존재하지 않는 userId_포인트 충전 실패`() {
        // given
        val invalidUserId = 999
        every { userRepository.findById(invalidUserId) } returns java.util.Optional.empty()

        // when
        val exception = assertThrows<UserNotFoundException> {
            userService.chargePoint(invalidUserId, 1000)
        }

        // then
        assertEquals("$invalidUserId 번 사용자를 찾을 수 없습니다.", exception.message)
        verify(exactly = 1) { userRepository.findById(invalidUserId) }
    }

    @Test
    fun `포인트 충전 성공`() {
        // given
        val userId = 1
        val pointsToCharge = 1000
        val user = User(username = "testUser", password = "hashedPassword").apply {
            id = userId
            points = 5000
        }

        every { userRepository.findById(userId) } returns java.util.Optional.of(user)

        // when
        val response: UserPointChargeResponse = userService.chargePoint(userId, pointsToCharge)

        // then
        assertEquals(userId, response.userId)
        assertEquals("testUser", response.username)
        assertEquals(pointsToCharge, response.chargedPoints)
        assertEquals(6000, response.totalPoints) // 기존 5000 + 충전 1000

        verify(exactly = 1) { userRepository.findById(userId) }
    }
}
