package com.sovesky.vocabenhancer.services

import com.sovesky.vocabenhancer.dto.thessaurus.ThessaurusDTO
import com.sovesky.vocabenhancer.mapper.VocabMapper
import com.sovesky.vocabenhancer.repositories.VocabRepository
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

    var ocurrences = mutableMapOf<String, Int>()

    companion object{
        const val THESSAURUS_BASE_URL = "https://www.dictionaryapi.com/api/v3/references/thesaurus/json/closure"
    }

    override fun parseWord(name: String): String {
        // 1. Check inputWord count
        // 2. Larger than 1, get synonyms
        // 3. Parse synonyms
        // 4. Synonym count lower than inputWord count then return synonym and increment count
        // 5. Else, get to next Synonym and repeat
        // 6. No synonyms available, return same word and increase count
        val wordVal = ocurrences[name]
        return wordVal?.let{
            getSynonyms(name)
                    .find { ocurrences.getOrDefault(it,0) < wordVal }
                    ?.apply { ocurrences[this] = ocurrences.getOrDefault(this,0)+1 }
                    ?:name
        } ?: name.apply { ocurrences[name] = 1 }

    }

    override fun getSynonyms(name: String): Set<String> {
        var set = getSynonymsFromDB(name)
        if(set.isEmpty()){
            set = getSynonymsFromThessaurus(name)
        }
        return set
    }

    override fun getSynonymsFromThessaurus(name: String): Set<String> {
        val ucb: UriComponentsBuilder = UriComponentsBuilder.fromUriString(THESSAURUS_BASE_URL)
                .queryParam("key", thessaurusKey)
        val obj: ThessaurusDTO = rt.getForObject(ucb.toUriString())
        return vocabMapper.thessaurusToVocab(obj).synonyms ?: emptySet()

    }

    override fun getSynonymsFromDB(name: String): Set<String> {
        // If no synonyms found, return empty set
        return vocabRepository.findByName(name)?.synonyms ?: emptySet()
    }
}