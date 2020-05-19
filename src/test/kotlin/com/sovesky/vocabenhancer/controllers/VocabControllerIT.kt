package com.sovesky.vocabenhancer.controllers

import com.fasterxml.jackson.databind.JsonNode
import com.sovesky.vocabenhancer.controller.VocabController
import com.sovesky.vocabenhancer.services.VocabService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.test.web.client.postForObject
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.HashMap


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class VocabControllerIT {
    @Autowired
    lateinit var vocabService: VocabService

    val testRestTemplate = TestRestTemplate()

    @Test
    fun `Post request to translate single word`(){
        val url = VocabController.BASE_URL+"/translation"
        //Java object to parse to JSON
        val postMap: MutableMap<String, Any> = HashMap()
        postMap["text"] = "closure"

        val jsonNode
                = testRestTemplate.postForEntity(url, postMap, JsonNode::class.java)

        assertThat(jsonNode.statusCode, equalTo(HttpStatus.OK));
//        assertThat(jsonNode.body, equalTo())

    }

    @Test
    fun `Post request to translate multiple words`(){

    }
}