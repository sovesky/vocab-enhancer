package com.sovesky.vocabenhancer.controllers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.sovesky.vocabenhancer.controller.VocabController
import com.sovesky.vocabenhancer.dto.vocabdto.VocabDTO
import com.sovesky.vocabenhancer.exceptionhandling.ControllerAdvice
import com.sovesky.vocabenhancer.services.VocabService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import javax.validation.ConstraintViolationException
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@WebMvcTest
class VocabControllerTest {

    @MockBean
    lateinit var vocabService: VocabService
    @Autowired
    lateinit var vocabController: VocabController
    @Autowired
    lateinit var mockMvc: MockMvc
    @Autowired
    lateinit var objectMapper: ObjectMapper

    val translateOperation = "/translation"

    @Test
    fun `improveText - Exception thrown`(){
        val input = VocabDTO("")

        // Empty String
        mockMvc.perform(post(VocabController.BASE_URL+translateOperation)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest)

        // Null body
        mockMvc.perform(post(VocabController.BASE_URL+translateOperation)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(null))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest)

        // Small string
        mockMvc.perform(post(VocabController.BASE_URL+translateOperation)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input.apply { name = ".." }))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest)

    }

    @Test
    fun `improveText - valid request`(){
        val input = "closure"
        val inputVocabDTO = VocabDTO(input)

        `when`(vocabService.parseWord(anyString(), anyMap())).thenReturn(input)

        mockMvc.perform(post(VocabController.BASE_URL+translateOperation)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputVocabDTO))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().json("{\"text\": \"closure\"}"))
    }

}