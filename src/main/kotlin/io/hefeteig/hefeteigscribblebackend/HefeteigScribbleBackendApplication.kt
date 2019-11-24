package io.hefeteig.hefeteigscribblebackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.TaskScheduler




@SpringBootApplication
class HefeteigScribbleBackendApplication

fun main(args: Array<String>) {
    runApplication<HefeteigScribbleBackendApplication>(*args)
}
