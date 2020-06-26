package com.sovesky.vocabenhancer.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class CustomConfig {
    // This way we don't have to create a new instance of a class for each incoming request

    @Bean
    fun restTemplate(rtb: RestTemplateBuilder): RestTemplate{
        return rtb.build()
    }

    @Bean
    fun objectMapper(): ObjectMapper{
        return ObjectMapper()
    }

}