package de.akrebs.proto.edifact

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment


@SpringBootApplication(scanBasePackages = ["de.akrebs.proto.edifact"])
class EdifactReactiveServerApplication {

	companion object {
		val LOGGER: Logger = LoggerFactory.getLogger("EdifactReactiveServerApplication")
	}

	@Bean
	fun applicationRunner(environment: Environment): ApplicationRunner? {
		return ApplicationRunner { _: ApplicationArguments? ->
			LOGGER.info(
				"message from application.properties " + environment.getProperty(
					"edifact.edi-1.ip"
				)
			)
		}
	}

}


fun main(args: Array<String>) {
	runApplication<EdifactReactiveServerApplication>(*args)
}
