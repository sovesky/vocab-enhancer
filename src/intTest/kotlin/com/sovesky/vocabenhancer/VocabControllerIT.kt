package com.sovesky.vocabenhancer

import com.fasterxml.jackson.databind.JsonNode
import com.sovesky.vocabenhancer.controller.VocabController
import com.sovesky.vocabenhancer.services.VocabService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import java.util.*


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
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
        postMap["text"] = "closure"

        val jsonNode = testRestTemplate.postForEntity("${getRootUrl()}${requestOperation}", postMap, JsonNode::class.java)

        assertThat(jsonNode.statusCode, equalTo(HttpStatus.OK));
        assertThat(jsonNode.body?.get("text")?.asText(), `is`("closure"))

    }

    @Test
    fun `post request to translate multiple repeated words`(){
        postMap["text"] = "closure closure closure"

        val jsonNode = testRestTemplate.postForEntity("${getRootUrl()}${requestOperation}", postMap, JsonNode::class.java)

        assertThat(jsonNode.statusCode, equalTo(HttpStatus.OK));
        assertThat(jsonNode.body?.get("text")?.asText(), `is`("closure arrest arrestment"))
    }

    @Test
    fun `post request to translate multiple different words`(){
        postMap["text"] = "closure closure life life"

        val jsonNode = testRestTemplate.postForEntity("${getRootUrl()}${requestOperation}", postMap, JsonNode::class.java)

        assertThat(jsonNode.statusCode, equalTo(HttpStatus.OK));
        assertThat(jsonNode.body?.get("text")?.asText(), `is`("closure arrest life bio"))
    }

    @Test
    fun `post request to translate multiple different words with punctuation`(){
        postMap["text"] = "closure closure, life life!"

        val jsonNode = testRestTemplate.postForEntity("${getRootUrl()}${requestOperation}", postMap, JsonNode::class.java)

        assertThat(jsonNode.statusCode, equalTo(HttpStatus.OK));
        assertThat(jsonNode.body?.get("text")?.asText(), `is`("closure arrest, life bio!"))
    }

    @Test
    fun `post request to translate multiple punctuation`(){
        postMap["text"] = "!,\\.<\"$^*"

        val jsonNode = testRestTemplate.postForEntity("${getRootUrl()}${requestOperation}", postMap, JsonNode::class.java)

        assertThat(jsonNode.statusCode, equalTo(HttpStatus.OK));
        assertThat(jsonNode.body?.get("text")?.asText(), `is`("!,\\.<\"\$^*"))
    }

    @Test
    fun `post request to translate multiple spaces`(){
        postMap["text"] = "       "

        val jsonNode = testRestTemplate.postForEntity("${getRootUrl()}${requestOperation}", postMap, JsonNode::class.java)

        assertThat(jsonNode.statusCode, equalTo(HttpStatus.OK));
        assertThat(jsonNode.body?.get("text")?.asText(), `is`("       "))
    }

    @Test
    fun `post request to translate multiple words with different capitalizations`(){
        postMap["text"] = "closure CLOSURE Closure"

        val jsonNode = testRestTemplate.postForEntity("${getRootUrl()}${requestOperation}", postMap, JsonNode::class.java)

        assertThat(jsonNode.statusCode, equalTo(HttpStatus.OK));
        assertThat(jsonNode.body?.get("text")?.asText(), `is`("closure ARREST Arrestment"))
    }

    @Test
    fun `post request to translate words with synonym appending`(){
        postMap["text"] = "closure closure closure"

        val jsonNode = testRestTemplate.postForEntity("${getRootUrl()}${requestOperation}?append=true", postMap, JsonNode::class.java)

        assertThat(jsonNode.statusCode, equalTo(HttpStatus.OK));
        assertThat(jsonNode.body?.get("text")?.asText(), `is`("closure closure<arrest> closure<arrestment>"))
    }

    @Test
    fun `post request to translate words with failed input validation`(){
        postMap["text"] = ".."

        val jsonNode = testRestTemplate.postForEntity("${getRootUrl()}${requestOperation}", postMap, JsonNode::class.java)

        assertThat(jsonNode.statusCode, equalTo(HttpStatus.BAD_REQUEST));
        assertThat(jsonNode.body?.get("errors")?.get("name")?.asText(), not(nullValue()))
    }
}