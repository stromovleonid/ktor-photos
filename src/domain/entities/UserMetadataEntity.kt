package io.photos.domain.entities

data class UserMetadataEntity(val id: Long, val username: UsernameEntity): Entity

data class UsernameEntity(val username: String): Entity