package plus.voyage.framework

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class VoyageBoardApplication

fun main(args: Array<String>) {
	runApplication<VoyageBoardApplication>(*args)
}
