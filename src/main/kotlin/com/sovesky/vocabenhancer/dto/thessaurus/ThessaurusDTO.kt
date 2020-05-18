package com.sovesky.vocabenhancer.dto.thessaurus

import com.fasterxml.jackson.annotation.*
import com.sovesky.vocabenhancer.dto.thessaurus.Def
import com.sovesky.vocabenhancer.dto.thessaurus.Hwi
import com.sovesky.vocabenhancer.dto.thessaurus.Meta


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("meta", "hwi", "fl", "def", "shortdef")
class ThessaurusDTO {
    @get:JsonProperty("meta")
    @set:JsonProperty("meta")
    @JsonProperty("meta")
    var meta: Meta? = null

    @get:JsonProperty("hwi")
    @set:JsonProperty("hwi")
    @JsonProperty("hwi")
    var hwi: Hwi? = null

    @get:JsonProperty("fl")
    @set:JsonProperty("fl")
    @JsonProperty("fl")
    var fl: String? = null

    @get:JsonProperty("def")
    @set:JsonProperty("def")
    @JsonProperty("def")
    var def: List<Def>? = null

    @get:JsonProperty("shortdef")
    @set:JsonProperty("shortdef")
    @JsonProperty("shortdef")
    var shortdef: List<String>? = null

}