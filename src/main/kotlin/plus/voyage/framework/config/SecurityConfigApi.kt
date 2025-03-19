package plus.voyage.framework.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RegexRequestMatcher
import plus.voyage.framework.auth.JwtAuthenticationFilter
import plus.voyage.framework.auth.JwtUtil
import plus.voyage.framework.exception.JwtAccessDeniedHandler
import plus.voyage.framework.exception.JwtAuthenticationEntryPoint

@Configuration
@EnableWebSecurity
@Profile("api")
class SecurityConfigApi(
    private val jwtAccessDeniedHandler: JwtAccessDeniedHandler,
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val jwtUtil: JwtUtil,
    private val userDetailService: UserDetailsService
) {
    @Bean
    fun securityFilterChainApi(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/users/login",
                    "/users/signup"
                ).permitAll()
                it.requestMatchers(
                    RegexRequestMatcher("/users", "GET"),
                    RegexRequestMatcher("^/users/\\d+/role$", "PATCH"),
                    RegexRequestMatcher("^/users/\\d+/points$", "POST"),
                    AntPathRequestMatcher("/coffee", "POST")
                ).hasAuthority("ROLE_ADMIN")
                it.anyRequest().authenticated()
            }
            .addFilterBefore(
                JwtAuthenticationFilter(jwtUtil, userDetailService),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .oauth2ResourceServer { it.jwt { } }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .exceptionHandling {
                it.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                it.accessDeniedHandler(jwtAccessDeniedHandler)
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
    fun jwtDecoder(): NimbusJwtDecoder {
        return NimbusJwtDecoder.withSecretKey(jwtUtil.getSecretKey()).build()
    }

    @Bean
    fun jwtAuthenticationConverter(): Converter<Jwt, AbstractAuthenticationToken> {
        return JwtAuthenticationConverter().apply {
            setJwtGrantedAuthoritiesConverter { jwt: Jwt ->
                val roles = jwt.getClaimAsStringList("roles") ?: listOf()
                println("✅ JWT Roles from token: $roles")
                roles.map { SimpleGrantedAuthority(it) }
            }
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
