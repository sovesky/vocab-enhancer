package com.sovesky.vocabenhancer.services

import com.sovesky.vocabenhancer.domain.Vocab
import com.sovesky.vocabenhancer.dto.thessaurus.ThessaurusDTO
import com.sovesky.vocabenhancer.getLogger
import com.sovesky.vocabenhancer.mapper.VocabMapper
import com.sovesky.vocabenhancer.repositories.VocabRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import org.springframework.web.util.UriComponentsBuilder


@Service
class VocabServiceImpl(private val vocabRepository: VocabRepository,
                       private val vocabMapper: VocabMapper,
                       private val rt: RestTemplate) : VocabService {

    @Value("\${thessaurus.apikey}")
    lateinit var thessaurusKey: String

    companion object {
        const val THESSAURUS_BASE_URL = "https://www.dictionaryapi.com/api/v3/references/thesaurus/json/"
    }

    private var ocurrences = mutableMapOf<String, Int>() // fixme - new one created for each word?
    private val logger = getLogger(javaClass)

    override fun parseWord(name: String): String {
        // 1. Check inputWord count
        // 2. Larger than 1, get synonyms
        // 3. Parse synonyms
        // 4. Synonym count lower than inputWord count then return synonym and increment count
        // 5. Else, get to next Synonym and repeat
        // 6. No synonyms available, return same word and increase count
        logger.debug("New word to be parsed '$name'")
        val wordVal = ocurrences[name]
        return wordVal?.let{
            logger.debug("Word '$name' ocurrence exists")
            getSynonyms(name)
                    .find { ocurrences.getOrDefault(it,0) < wordVal }
                    ?.apply { ocurrences[this] = ocurrences.getOrDefault(this,0)+1 }
                    ?:name
        } ?: name.apply { ocurrences[name] = 1 }.also { logger.debug("First ocurrence of word '$name'") }

    }

    override fun getSynonyms(name: String): Set<String> {
        var set = getSynonymsFromDB(name)
        if(set.isEmpty()){
            set = getSynonymsFromThessaurus(name)
        }
        logger.debug("Synonym results for word '$name' are: $set")
        return set
    }

    override fun getSynonymsFromThessaurus(name: String): Set<String> {
        logger.debug("Getting synonyms from Thessaurus for word '$name'")
        val ucb: UriComponentsBuilder = UriComponentsBuilder.fromUriString(THESSAURUS_BASE_URL)
                .queryParam("key", thessaurusKey)
        val obj: ThessaurusDTO = rt.getForObject(ucb.toUriString())
        check(obj.meta != null) { "Word could not be found on Thessaurus" } // todo refactor this
        val vocab: Vocab = vocabMapper.thessaurusToVocab(obj)
        vocabRepository.save(vocab)
        return vocab.synonyms ?: emptySet()
    }

    override fun getSynonymsFromDB(name: String): Set<String> {
        // If no synonyms found, return empty set
        return vocabRepository.findByName(name)?.synonyms ?: emptySet()
    }
}