package com.sovesky.vocabenhancer

import kotlinx.coroutines.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class VocabEnhancerApplication

fun main(args: Array<String>) = runBlocking<Unit>{
    runApplication<VocabEnhancerApplication>(*args)
}

fun getLogger(forClass: Class<*>): Logger =
        LoggerFactory.getLogger(forClass)
