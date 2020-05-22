package com.sovesky.vocabenhancer.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.sovesky.vocabenhancer.dto.thessaurus.Meta
import com.sovesky.vocabenhancer.dto.thessaurus.ThessaurusDTO

// Implemented separately because cannot auto bind Thessaurus JSON to POJO
// "def" element has a child great-grand child that is of Any() type
// This way we extract only the info we need
fun buildThessaurusDTOFromJSON(nodeArr: ArrayNode, objectMapper: ObjectMapper): ThessaurusDTO {
    val thessaurusDTO = ThessaurusDTO()
    if(nodeArr.isArray && nodeArr.size() > 0 && nodeArr.get(0).get("meta") != null)
        thessaurusDTO.meta = objectMapper.convertValue(nodeArr.get(0).get("meta"), Meta::class.java)
    return thessaurusDTO
}