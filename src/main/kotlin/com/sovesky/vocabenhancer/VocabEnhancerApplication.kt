package com.sovesky.vocabenhancer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class VocabEnhancerApplication

fun main(args: Array<String>) {
    runApplication<VocabEnhancerApplication>(*args)
}
