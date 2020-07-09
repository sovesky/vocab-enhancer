package com.sovesky.vocabenhancer.dto.vocabdto

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

// https://stackoverflow.com/questions/36515094/kotlin-and-valid-spring-annotation
data class VocabDTO(@field:Size(min=3) @field:NotNull @field:JsonProperty("text")
               var text: String?) {
    constructor() : this(null)
}