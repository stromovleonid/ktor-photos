package io.photos.domain.entities

import io.photos.domain.utils.AuthTokenProducer

data class UserEntity(val id: Long, val login: String, val password: String): Entity {
    fun getToken(producer: AuthTokenProducer) = producer.produce(this.id)
}