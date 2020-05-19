package com.sovesky.vocabenhancer.services

import com.sovesky.vocabenhancer.domain.Vocab
import com.sovesky.vocabenhancer.dto.thessaurus.Hwi
import com.sovesky.vocabenhancer.dto.thessaurus.Meta
import com.sovesky.vocabenhancer.dto.thessaurus.ThessaurusDTO
import com.sovesky.vocabenhancer.mapper.VocabMapper
import com.sovesky.vocabenhancer.repositories.VocabRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mapstruct.factory.Mappers
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.web.client.RestTemplate


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VocabServiceTest {

    @Mock
    lateinit var vocabRepository: VocabRepository
    @Mock
    lateinit var restTemplate: RestTemplate

    lateinit var vocabService: VocabService
    lateinit var name: String
    lateinit var synonyms: Array<String>
    lateinit var vocab: Vocab
    lateinit var dto: ThessaurusDTO

    @BeforeAll
    internal fun beforeAll() {
        MockitoAnnotations.initMocks(this)
        vocabService = VocabServiceImpl(vocabRepository, Mappers.getMapper(VocabMapper::class.java), restTemplate)

        name = "closure"
        synonyms = arrayOf("arrest", "cease", "close")

        vocab = Vocab(name = name, synonyms = synonyms.toSet())
                .apply { vocabRepository.save(this) }
        dto = ThessaurusDTO()
                .apply { this.hwi = Hwi().apply { this.hw = name } }
                .apply { this.meta = Meta().apply { this.syns = listOf(synonyms.toList()) } }

        // Initializing a @Value annotated variable
        ReflectionTestUtils.setField(vocabService, "thessaurusKey", "mock");
    }

    @Test
    fun `Get Vocab From DB When String Provided`(){
        // given
        `when`(vocabRepository.findByName(anyString())).thenReturn(vocab)
        // when
        val resVocab = vocabService.getSynonymsFromDB(name)
        // then
        assertThat(vocab.synonyms, equalTo(resVocab))
    }

    @Test
    fun `Get Synonyms from Thessaurus When String Provided`(){
        // give
        `when`(restTemplate.getForObject<ThessaurusDTO>(anyString(), any())).thenReturn(dto)
        `when`(vocabRepository.save(any(Vocab::class.java))).thenReturn(vocab)
        // when
        val resVocab = vocabService.getSynonymsFromThessaurus(name)
        // then
        assertThat(resVocab?.isEmpty(), `is`(false))
        assertThat(dto.meta?.syns?.get(0)?.toSet(), equalTo(resVocab))
    }

    @Test
    fun `Get Synonyms Parent Method - from Thessaurus`(){
        val vocabServiceSpy: VocabService = Mockito.spy(vocabService)
        doReturn(setOf<String>()).`when`(vocabServiceSpy).getSynonymsFromDB(anyString())
        doReturn(setOf<String>()).`when`(vocabServiceSpy).getSynonymsFromThessaurus(anyString())
        vocabServiceSpy.getSynonyms("foo")
        verify(vocabServiceSpy, times(1)).getSynonymsFromDB(anyString())
        verify(vocabServiceSpy, times(1)).getSynonymsFromThessaurus(anyString())
    }

    @Test
    fun `Get Synonyms Parent Method - from DB`(){
        val vocabServiceSpy: VocabService = spy(vocabService)
        doReturn(setOf("foo")).`when`(vocabServiceSpy).getSynonymsFromDB(anyString())
        doReturn(setOf<String>()).`when`(vocabServiceSpy).getSynonymsFromThessaurus(anyString())
        vocabServiceSpy.getSynonyms("foo")
        verify(vocabServiceSpy, times(1)).getSynonymsFromDB(anyString())
        verify(vocabServiceSpy, never()).getSynonymsFromThessaurus(anyString())
    }

    @Test
    fun `Test parseWord New Synonym`(){
        // given
        val vocabServiceSpy: VocabServiceImpl = spy(vocabService) as VocabServiceImpl
        vocabServiceSpy.ocurrences = mutableMapOf("closure" to 1)
        doReturn(synonyms.toSet()).`when`(vocabServiceSpy).getSynonyms(anyString())
        // when
        val word = vocabServiceSpy.parseWord(name)
        // then
        verify(vocabServiceSpy, times(1)).getSynonyms(anyString())
        assertThat(word, `is`(synonyms[0]))
    }

    @Test
    fun `Test parseWord Current Word`(){
        // given
        val vocabServiceSpy: VocabService = spy(vocabService)
        doReturn(emptySet<String>()).`when`(vocabServiceSpy).getSynonyms(anyString())
        // when
        val word = vocabService.parseWord(name)
        // then
        assertThat(word, `is`(name))
    }
}