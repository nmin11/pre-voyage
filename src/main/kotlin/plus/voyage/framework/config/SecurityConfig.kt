package plus.voyage.framework.config

import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler
import org.springframework.security.web.SecurityFilterChain
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Value("\${jwt.public.key}")
    lateinit var publicKey: RSAPublicKey

    @Value("\${jwt.private.key}")
    lateinit var privateKey: RSAPrivateKey

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/login",
                    "/register",
                    "/users/login",
                    "/users/signup"
                ).permitAll()
                it.requestMatchers("/h2-console/**").permitAll()
                it.requestMatchers("/admin/**").hasRole("ADMIN")
                it.anyRequest().authenticated()
            }
            .headers { header -> header.frameOptions { frame -> frame.sameOrigin() } }
            .httpBasic(Customizer.withDefaults())
            .formLogin(Customizer.withDefaults())
            .oauth2ResourceServer { it.jwt {  } }
            .exceptionHandling {
                it.authenticationEntryPoint(BearerTokenAuthenticationEntryPoint())
                it.accessDeniedHandler(BearerTokenAccessDeniedHandler())
            }

        return http.build()
    }

    @Bean
    fun jwtDecoder(): NimbusJwtDecoder {
        return NimbusJwtDecoder.withPublicKey(this.publicKey).build()
    }

    @Bean
    fun jwtEncoder(): NimbusJwtEncoder {
        val jwk: JWK = RSAKey.Builder(this.publicKey).privateKey(this.privateKey).build()
        val jwkSet: JWKSource<SecurityContext> = ImmutableJWKSet(JWKSet(jwk))
        return NimbusJwtEncoder(jwkSet)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
