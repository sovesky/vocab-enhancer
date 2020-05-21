package com.sovesky.vocabenhancer.controller

import com.sovesky.vocabenhancer.dto.vocabdto.VocabDTO
import com.sovesky.vocabenhancer.services.VocabService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping(VocabController.BASE_URL)
class VocabController(val vocabService: VocabService) {

    companion object {
        const val BASE_URL = "/vocab-enhancer"
    }

    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping(value = ["/translation"])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun handleFileUpload(@Valid @RequestBody input: VocabDTO) : Map<String, String> {
        logger.debug("Beginning new POST translation request...")
        val occurrences = mutableMapOf<String, Int>()

//        return Regex("""(\b\w+\b)|\W+""")
        return Regex("""(\b\w+\b)|\W+""")
                .findAll(input.name!!) // We have validated input beforehand
                .joinToString("") { vocabService.parseWord(it.value, occurrences) }
                .run { mapOf("text" to this) }
    }
}