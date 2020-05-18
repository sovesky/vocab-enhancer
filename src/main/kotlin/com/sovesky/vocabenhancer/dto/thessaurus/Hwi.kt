package com.sovesky.vocabenhancer.dto.thessaurus

import com.fasterxml.jackson.annotation.*


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("hw")
class Hwi {
    @get:JsonProperty("hw")
    @set:JsonProperty("hw")
    @JsonProperty("hw")
    var hw: String? = null
}