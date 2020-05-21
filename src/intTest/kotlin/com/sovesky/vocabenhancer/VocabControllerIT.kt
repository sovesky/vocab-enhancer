package com.sovesky.vocabenhancer

import com.fasterxml.jackson.databind.JsonNode
import com.sovesky.vocabenhancer.controller.VocabController
import com.sovesky.vocabenhancer.services.VocabService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import java.util.*


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VocabControllerIT @Autowired constructor (private val testRestTemplate: TestRestTemplate,
                                                private val vocabService: VocabService){

    @LocalServerPort
    private var port: Int = 0
    val requestOperation = "/translation"
    private fun getRootUrl(): String? = "http://localhost:$port${VocabController.BASE_URL}"
    lateinit var postMap: MutableMap<String, Any>

    @BeforeEach
    fun setUp(){
        postMap = HashMap()
    }

    @Test
    fun `post request to translate single word`(){
        //Java object to parse to JSON
//        val postMap: MutableMap<String, Any> = HashMap()
        postMap["text"] = "closure"

        val jsonNode = testRestTemplate.postForEntity("${getRootUrl()}${requestOperation}", postMap, JsonNode::class.java)

        assertThat(jsonNode.statusCode, equalTo(HttpStatus.OK));
        assertThat(jsonNode.body?.get("text")?.asText(), `is`("closure"))

    }

    @Test
    fun `post request to translate multiple repeated words`(){
        //Java object to parse to JSON
//        val postMap: MutableMap<String, Any> = HashMap()
        postMap["text"] = "closure closure closure"

        val jsonNode = testRestTemplate.postForEntity("${getRootUrl()}${requestOperation}", postMap, JsonNode::class.java)

        assertThat(jsonNode.statusCode, equalTo(HttpStatus.OK));
        assertThat(jsonNode.body?.get("text")?.asText(), `is`("closure arrest arrestment"))
    }

    @Test
    fun `post request to translate multiple different words`(){
        //Java object to parse to JSON
//        val postMap: MutableMap<String, Any> = HashMap()
        postMap["text"] = "Closure Closure Life Life"

        val jsonNode = testRestTemplate.postForEntity("${getRootUrl()}${requestOperation}", postMap, JsonNode::class.java)

        assertThat(jsonNode.statusCode, equalTo(HttpStatus.OK));
        assertThat(jsonNode.body?.get("text")?.asText(), `is`("closure arrest life bio"))
    }

    @Test
    fun `post request to translate multiple different words with punctuation`(){
        //Java object to parse to JSON
//        val postMap: MutableMap<String, Any> = HashMap()
        postMap["text"] = "Closure Closure, Life Life!"

        val jsonNode = testRestTemplate.postForEntity("${getRootUrl()}${requestOperation}", postMap, JsonNode::class.java)

        assertThat(jsonNode.statusCode, equalTo(HttpStatus.OK));
        assertThat(jsonNode.body?.get("text")?.asText(), `is`("closure arrest, life bio!"))
    }

    @Test
    fun `post request to translate multiple punctuation`(){
        //Java object to parse to JSON
//        val postMap: MutableMap<String, Any> = HashMap()
        postMap["text"] = "!,\\.<\"$^*"

        val jsonNode = testRestTemplate.postForEntity("${getRootUrl()}${requestOperation}", postMap, JsonNode::class.java)

        assertThat(jsonNode.statusCode, equalTo(HttpStatus.OK));
        assertThat(jsonNode.body?.get("text")?.asText(), `is`("!,\\.<\"\$^*"))
    }

    @Test
    fun `post request to translate multiple spaces`(){
        //Java object to parse to JSON
//        val postMap: MutableMap<String, Any> = HashMap()
        postMap["text"] = "       "

        val jsonNode = testRestTemplate.postForEntity("${getRootUrl()}${requestOperation}", postMap, JsonNode::class.java)

        assertThat(jsonNode.statusCode, equalTo(HttpStatus.OK));
        assertThat(jsonNode.body?.get("text")?.asText(), `is`("       "))
    }

    @Test
    fun `post request to translate multiple words with different capitalizations`(){
        //Java object to parse to JSON
//        val postMap: MutableMap<String, Any> = HashMap()
        postMap["text"] = "closure CLOSURE Closure"

        val jsonNode = testRestTemplate.postForEntity("${getRootUrl()}${requestOperation}", postMap, JsonNode::class.java)

        assertThat(jsonNode.statusCode, equalTo(HttpStatus.OK));
        assertThat(jsonNode.body?.get("text")?.asText(), `is`("closure ARREST Arrestment"))
    }

    // TODO Create tests with capital letters
}