package plus.voyage.framework

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableAsync

@EnableAsync
@EnableCaching
@SpringBootApplication
class PreVoyageApplication

fun main(args: Array<String>) {
	runApplication<PreVoyageApplication>(*args)
}
