package com.sovesky.vocabenhancer.repositories

import com.sovesky.vocabenhancer.domain.Vocab
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest


@DataMongoTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VocabRepositoryTest(@Autowired val vocabRepository: VocabRepository) {

    private val vocab: Vocab
            = Vocab(name = "closure", synonyms = setOf("arrest", "cease", "close"))
                .apply { vocabRepository.save(this) }

    @Test
    fun `When save then return Vocab`() {
        // given
        // when
        // then
        assertThat(vocabRepository.findAll())
                .extracting("name")
                .containsExactly("closure")
    }

    @Test
    fun `When findByName then return Vocab`() {
        // given
        // when
        val returnedVocab = vocabRepository.findByName(vocab.name!!)
        // then
        assertThat(returnedVocab?.name, equalTo(vocab.name))
    }

    @Test
    fun `When findByName empty`() {
        // given
        // when
        val returnedVocab = vocabRepository.findByName("foo")
        // then
        assertThat(returnedVocab, `is`(nullValue()))
    }
}