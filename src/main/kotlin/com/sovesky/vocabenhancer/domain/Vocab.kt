package com.sovesky.vocabenhancer.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "vocab")
data class Vocab(@Id var _id: ObjectId? = null, var name: String?, var synonyms: Set<String>?, var lastUpdate: LocalDateTime? = null) {
    // Necessary for MapStruct
    constructor() : this(null, null, null, null)
}
