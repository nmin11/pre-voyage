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
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler
import org.springframework.security.web.SecurityFilterChain
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

@Configuration
@EnableWebSecurity
@Profile("thymeleaf")
class SecurityConfigThymeleaf(
    private val userDetailService: UserDetailsService
) {
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
                    "/users/signup",
                    "/css/**",
                    "/js/**",
                    "/images/**"
                ).permitAll()
                it.requestMatchers("/admin/**").hasRole("ADMIN")
                it.anyRequest().authenticated()
            }
            .formLogin {
                it.loginPage("/login")
                it.loginProcessingUrl("/users/login")
                it.defaultSuccessUrl("/boards")
                it.failureUrl("/login?error=true")
            }
            .logout {
                it.logoutUrl("/logout")
                it.logoutSuccessUrl("/login?logout=true")
            }
            .headers { header -> header.frameOptions { frame -> frame.sameOrigin() } }
            .exceptionHandling {
                it.authenticationEntryPoint { _, response, _ ->
                    response.sendRedirect("/login")
                }
                it.accessDeniedHandler(BearerTokenAccessDeniedHandler())
            }

        return http.build()
    }

    @Bean
    fun authenticationManager(): AuthenticationManager {
        return ProviderManager(listOf(DaoAuthenticationProvider().apply {
            setUserDetailsService(userDetailService)
            setPasswordEncoder(passwordEncoder())
        }))
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
