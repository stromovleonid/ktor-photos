package io.photos.domain.entities

data class UserMetadataEntity(val id: Long, val username: Username): Entity

data class Username(val username: String)