package plus.voyage.framework.auth

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtUtil(
    @Value("\${jwt.secret-key}") private val secretKeyString: String
) {
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(secretKeyString.toByteArray())
    private val expirationMs: Long = 1000 * 60 * 60

    fun generateToken(username: String, roles: List<String>): String {
        return Jwts.builder()
            .setSubject(username)
            .claim("roles", roles)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expirationMs))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .body

            claims.expiration.after(Date())
        } catch (e: Exception) {
            false
        }
    }

    fun extractUsername(token: String): String {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
            .parseClaimsJws(token)
            .body.subject
    }

    fun getSecretKey(): SecretKey = secretKey
}
