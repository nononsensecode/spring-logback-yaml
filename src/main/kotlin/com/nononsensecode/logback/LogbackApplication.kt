package com.nononsensecode.logback

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class LogbackApplication

fun main(args: Array<String>) {
    runApplication<LogbackApplication>(*args)
}