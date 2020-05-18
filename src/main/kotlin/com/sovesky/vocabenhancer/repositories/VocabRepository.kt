package com.sovesky.vocabenhancer.repositories

import com.sovesky.vocabenhancer.domain.Vocab
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface VocabRepository: MongoRepository<Vocab, ObjectId>{
    fun findByName(name: String): Vocab?
}