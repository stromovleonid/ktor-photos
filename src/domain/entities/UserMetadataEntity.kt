package io.photos.domain.entities

import java.util.*

data class UserMetadataEntity(
    val id: Long,
    val username: UsernameEntity,
    val createdAt: Date,
    val avatar: AvatarEntity
) : Entity

data class UsernameEntity(val username: String) : Entity

data class AvatarEntity(val url: String? = null) : Entity