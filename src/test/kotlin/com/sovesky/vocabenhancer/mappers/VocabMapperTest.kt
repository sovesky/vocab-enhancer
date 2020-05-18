package com.sovesky.vocabenhancer.mappers

import com.sovesky.vocabenhancer.domain.Vocab
import com.sovesky.vocabenhancer.dto.thessaurus.Hwi
import com.sovesky.vocabenhancer.dto.thessaurus.Meta
import com.sovesky.vocabenhancer.dto.thessaurus.ThessaurusDTO
import com.sovesky.vocabenhancer.mapper.VocabMapper
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.hamcrest.core.IsEqual.equalTo
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import java.time.LocalDateTime

class VocabMapperTest{

    private val vocabMapper: VocabMapper = Mappers.getMapper(VocabMapper::class.java)

    @Test
    fun testVocabToThessaurusDTO(){
        val vocab = Vocab( name = "closure",
                synonyms = setOf("arrest","cease", "close"),
                lastUpdate =  LocalDateTime.now())
        val thessaurusVocab = vocabMapper.vocabToThessaurus(vocab)

        assertThat(vocab.name, equalTo(thessaurusVocab.hwi?.hw))
        assertThat(vocab.synonyms, hasSize(thessaurusVocab.meta!!.syns!![0].size))
    }

    @Test
    fun thessaurusDTOToVocab(){
        var thessaurus = ThessaurusDTO()
                .apply { this.hwi = Hwi().also { it.hw = "closure" } }
                .apply { this.meta = Meta().also { it.syns = listOf(listOf("arrest","cease", "close")) } }

        val vocab = vocabMapper.thessaurusToVocab(thessaurus)

        assertThat(thessaurus.hwi?.hw, equalTo(vocab.name))
        assertThat(thessaurus.meta!!.syns!![0], hasSize(vocab.synonyms!!.size))
    }
}