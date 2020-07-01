package com.sovesky.vocabenhancer.mapper

import com.sovesky.vocabenhancer.domain.Vocab
import com.sovesky.vocabenhancer.dto.thessaurus.ThessaurusDTO
import org.mapstruct.*

@Mapper(componentModel = "spring")
abstract class VocabMapper {

    @Mappings(
            Mapping(target = "_id", ignore = true),
            Mapping(target = "lastUpdate", ignore = true),
            Mapping(source = "meta.id", target = "name"),
            Mapping(source = "meta.syns", target = "synonyms")
    )
    abstract fun thessaurusToVocab(thessaurusDTO: ThessaurusDTO): Vocab

    @Mappings(
            Mapping(target = "meta.id", source = "name"),
            Mapping(target = "meta.syns", source = "synonyms"),
            Mapping(target = "fl", ignore = true),
            Mapping(target = "def", ignore = true),
            Mapping(target = "hwi", ignore = true),
            Mapping(target = "shortdef", ignore = true)
    )
    abstract fun vocabToThessaurus(vocab: Vocab): ThessaurusDTO

    fun metaSynsToSynonyms(nestedList: List<List<String>>?): Set<String> = nestedList?.flatten()?.toSet() ?: emptySet()
    fun synonymsToMetaSyns(list: Set<String>?): List<List<String>>? = mutableListOf(list?.toList() ?: emptyList())
}