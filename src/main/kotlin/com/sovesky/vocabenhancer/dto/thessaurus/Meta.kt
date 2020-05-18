package com.sovesky.vocabenhancer.dto.thessaurus

import com.fasterxml.jackson.annotation.*


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("id", "uuid", "src", "section", "target", "stems", "syns", "ants", "offensive")
class Meta {
    @get:JsonProperty("id")
    @set:JsonProperty("id")
    @JsonProperty("id")
    var id: String? = null

    @get:JsonProperty("uuid")
    @set:JsonProperty("uuid")
    @JsonProperty("uuid")
    var uuid: String? = null

    @get:JsonProperty("src")
    @set:JsonProperty("src")
    @JsonProperty("src")
    var src: String? = null

    @get:JsonProperty("section")
    @set:JsonProperty("section")
    @JsonProperty("section")
    var section: String? = null

    @get:JsonProperty("target")
    @set:JsonProperty("target")
    @JsonProperty("target")
    var target: Target? = null

    @get:JsonProperty("stems")
    @set:JsonProperty("stems")
    @JsonProperty("stems")
    var stems: List<String>? = null

    @get:JsonProperty("syns")
    @set:JsonProperty("syns")
    @JsonProperty("syns")
    var syns: List<List<String>>? = null

    @get:JsonProperty("ants")
    @set:JsonProperty("ants")
    @JsonProperty("ants")
    var ants: List<List<String>>? = null

    @get:JsonProperty("offensive")
    @set:JsonProperty("offensive")
    @JsonProperty("offensive")
    var offensive: Boolean? = null
}