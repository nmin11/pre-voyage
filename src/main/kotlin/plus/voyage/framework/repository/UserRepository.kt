package plus.voyage.framework.repository

import org.springframework.data.jpa.repository.JpaRepository
import plus.voyage.framework.entity.User

interface UserRepository : JpaRepository<User, Int> {
    fun findByUsername(username: String): User?
    fun existsByUsername(username: String): Boolean
}
