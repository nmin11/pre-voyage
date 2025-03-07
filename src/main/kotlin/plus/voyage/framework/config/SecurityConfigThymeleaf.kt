package plus.voyage.framework.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@Profile("thymeleaf")
class SecurityConfigThymeleaf {
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
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
