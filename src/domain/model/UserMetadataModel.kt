package io.photos.domain.model

data class UserMetadataModel(val id: Long, val username: UsernameModel): Model

data class UsernameModel(val username: String): Model