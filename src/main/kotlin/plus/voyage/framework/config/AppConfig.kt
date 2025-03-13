package plus.voyage.framework.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class AppConfig {
    @Bean
    fun restClient(): RestClient {
        return RestClient.create()
    }
}
