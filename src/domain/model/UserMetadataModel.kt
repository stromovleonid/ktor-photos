package io.photos.domain.model

import java.util.*

data class UserMetadataModel(val id: Long,
                             val username: String,
                             val createdAt: Date,
                             val avatarUrl: String?): Model

