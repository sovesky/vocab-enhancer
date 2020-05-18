package com.sovesky.vocabenhancer.dto.thessaurus

import com.fasterxml.jackson.annotation.*


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("sseq")
class Def {
    @get:JsonProperty("sseq")
    @set:JsonProperty("sseq")
    @JsonProperty("sseq")
    var sseq: List<List<List<String>>>? = null
}