package com.sovesky.vocabenhancer.controller

import com.sovesky.vocabenhancer.dto.vocabdto.VocabDTO
import com.sovesky.vocabenhancer.services.VocabService
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping(VocabController.BASE_URL)
@Validated
class VocabController(val vocabService: VocabService) {

    companion object {
        const val BASE_URL = "/vocab-enhancer"
    }

    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping(value = ["/translation"])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun handleFileUpload(@Valid @RequestBody input: VocabDTO,
                         @RequestParam("append") appendWord: Boolean?): Map<String, String> = runBlocking {
        logger.debug("Beginning new POST translation request...")
        val occurrences = mutableMapOf<String, Int>()

        val deferreds = Regex("""(\b\w+\b)|\W+""")
                .findAll(input.text!!) // We have validated input beforehand using @Valid on DTO
                .map {
                    async() {
                        assembleWord(it.value, vocabService.parseWord(it.value, occurrences), appendWord)
                    }
                }.toList()
        deferreds.awaitAll().joinToString("").run { mapOf("text" to this) }

    }

    // If append param is set to true we add <synonym> in front of the replacing word
    fun assembleWord(input: String, syn: String, replace: Boolean?): String {
        return if (replace == true && input != syn)
            "$input<$syn>"
        else
            syn
    }
}