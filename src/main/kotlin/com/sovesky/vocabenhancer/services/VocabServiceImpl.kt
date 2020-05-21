package com.sovesky.vocabenhancer.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.sovesky.vocabenhancer.domain.Vocab
import com.sovesky.vocabenhancer.mapper.VocabMapper
import com.sovesky.vocabenhancer.mapper.buildThessaurusDTOFromJSON
import com.sovesky.vocabenhancer.repositories.VocabRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForObject
import org.springframework.web.util.UriComponentsBuilder


@Service
class VocabServiceImpl(private val vocabRepository: VocabRepository,
                       private val vocabMapper: VocabMapper,
                       private val rt: RestTemplate,
                       private val objectMapper: ObjectMapper) : VocabService {

    @Value("\${thessaurus.apikey}")
    lateinit var thessaurusKey: String

    companion object {
        const val THESSAURUS_BASE_URL = "https://www.dictionaryapi.com/api/v3/references/thesaurus/json/"
    }

    private val logger = LoggerFactory.getLogger(javaClass)

    // 1. Check inputWord count
    // 2. Larger than 2, get synonyms
    // 3. Parse synonyms
    // 4. Synonym count lower than inputWord count then return synonym and increment count
    // 5. Else, get to next Synonym and repeat
    // 6. No synonyms available, return same word and increase count
    override fun parseWord(name: String, occurrences: MutableMap<String, Int>): String {
        logger.debug("New word to be parsed '$name'")

        // We don't want to translate spaces, digits, punctuation and basic words
        if (name.length < 2) return name

        val wordCount = occurrences[name.toLowerCase()]
        val result = wordCount?.let {
            logger.debug("Word '$name' ocurrence exists")
            getSynonyms(name.toLowerCase())
                    .find { occurrences.getOrDefault(it, 0) < wordCount }
                    ?.apply { occurrences[this] = occurrences.getOrDefault(this, 0) + 1 }
                    ?: name.also { logger.debug("Replacing word '$name' with new word '$it'") }
        } ?: name.apply { occurrences[name.toLowerCase()] = 1 }
                .also { logger.debug("First ocurrence of word '$name'") }

        return when {
            name.all { it.isUpperCase() } -> result.toUpperCase()
            name[0].isUpperCase() -> result.capitalize()
            else -> result
        }
    }

    override fun getSynonyms(name: String): Set<String> {
        var set = getSynonymsFromDB(name)
        if (set.isEmpty()) {
            set = getSynonymsFromThessaurus(name)
        }
        logger.debug("Synonym results for word '$name' are: $set")
        return set
    }

    override fun getSynonymsFromThessaurus(name: String): Set<String> {
        logger.debug("Getting synonyms from Thessaurus for word '$name'")
        val ucb = UriComponentsBuilder.fromUriString("$THESSAURUS_BASE_URL$name")
                .queryParam("key", thessaurusKey).toUriString()
        logger.debug("Target Thessaurus URL: '$ucb'")
        val node: ArrayNode = rt.postForObject(ucb)
        logger.trace(node.toPrettyString())
        val thessaurusDTO = buildThessaurusDTOFromJSON(node, objectMapper)
        check(thessaurusDTO.meta != null)
        { "Unexpected response format from Thessaurus" } // todo refactor this
        val vocab: Vocab = vocabMapper.thessaurusToVocab(thessaurusDTO)
        vocabRepository.save(vocab)
        return vocab.synonyms ?: emptySet()
    }

    override fun getSynonymsFromDB(name: String): Set<String> {
        logger.debug("Checking wird '$name' within DB...")
        return vocabRepository.findByName(name)?.synonyms.also { logger.debug("Word found in DB!") }
                ?: emptySet()
    }

}