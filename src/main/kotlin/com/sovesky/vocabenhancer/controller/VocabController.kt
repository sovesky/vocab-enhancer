package com.sovesky.vocabenhancer.controller

import com.sovesky.vocabenhancer.dto.vocabdto.VocabDTO
import com.sovesky.vocabenhancer.getLogger
import com.sovesky.vocabenhancer.services.VocabService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping(VocabController.BASE_URL)
class VocabController(val vocabService: VocabService) {

    companion object {
        const val BASE_URL = "/vocab-enhancer"
    }

    private val logger = getLogger(javaClass)

    @RequestMapping(value = ["/translation"])
    @ResponseStatus(HttpStatus.OK)
    fun handleFileUpload(@Valid @RequestBody input: VocabDTO) : String {
        logger.debug("Beginning new POST translation request...")
        return Regex("""\W+""")
                .split(input.name!!) // We have validated input beforehand
                .filter { it.length < 3 }
                .joinToString { vocabService.parseWord(it) }
    }
}