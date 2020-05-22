package com.sovesky.vocabenhancer.services

import com.sovesky.vocabenhancer.domain.Vocab

interface VocabService {
    fun parseWord(name: String, occurrences: MutableMap<String, Int>): String
    fun getSynonyms(name: String): Set<String>?
    fun getSynonymsFromThessaurus(name: String): Set<String>?
    fun getSynonymsFromDB(name: String): Set<String>?
}