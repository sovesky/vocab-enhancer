package com.sovesky.vocabenhancer.dto.thessaurus

import com.fasterxml.jackson.annotation.*


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("tuuid", "tsrc")
class Target {
    @get:JsonProperty("tuuid")
    @set:JsonProperty("tuuid")
    @JsonProperty("tuuid")
    var tuuid: String? = null

    @get:JsonProperty("tsrc")
    @set:JsonProperty("tsrc")
    @JsonProperty("tsrc")
    var tsrc: String? = null
}