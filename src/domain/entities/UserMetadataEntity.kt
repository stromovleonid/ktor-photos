package io.photos.domain.entities

import java.lang.IllegalStateException
import java.util.*

data class UserMetadataEntity(
    val id: UserIdEntity,
    val username: UsernameEntity,
    val createdAt: Date,
    val avatar: AvatarEntity
) : Entity {
    companion object {
        val MISSING_USER = UserMetadataEntity(
            UserIdEntity(0),
            UsernameEntity("User deleted ot not yet created"),
            Date(), AvatarEntity()
        )
    }
}

data class UsernameEntity(val username: String) : Entity

data class AvatarEntity(val url: String? = null) : Entity

data class UserIdEntity(val id: Long) {
    init {
        if (id < 0)
            throw IllegalStateException("id should be >= 0")
    }
}