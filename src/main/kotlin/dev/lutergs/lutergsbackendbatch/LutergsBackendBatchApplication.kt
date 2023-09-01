package dev.lutergs.lutergsbackendbatch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class LutergsBackendBatchApplication

fun main(args: Array<String>) {
    runApplication<LutergsBackendBatchApplication>(*args)
}
