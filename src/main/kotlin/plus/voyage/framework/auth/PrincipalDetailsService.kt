package plus.voyage.framework.auth

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import plus.voyage.framework.entity.User
import plus.voyage.framework.repository.UserRepository

@Service
class PrincipalDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails? {
        val user: User? = userRepository.findByUsername(username)
        return if (user != null) {
            PrincipalDetails(user)
        } else null
    }
}
