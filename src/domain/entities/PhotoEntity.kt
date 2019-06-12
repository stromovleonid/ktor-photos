package io.photos.domain.entities

import java.util.*

data class PhotoEntity(val id: PhotoIdEntity,
                       val authorId: UserIdEntity,
                       val url: String,
                       val metadata: PhotoMetadataEntity): Entity

data class PhotoMetadataEntity(val createdAt: Date): Entity

data class PhotoIdEntity(val id: Long)