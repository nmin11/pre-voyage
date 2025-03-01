package plus.voyage.framework.auth

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import plus.voyage.framework.entity.User

class PrincipalDetails(private var user: User) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val collection: MutableCollection<GrantedAuthority> = ArrayList()
        collection.add(GrantedAuthority { user.role.name })
        return collection
    }

    override fun getPassword(): String {
        return user.password
    }

    override fun getUsername(): String {
        return user.username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}
