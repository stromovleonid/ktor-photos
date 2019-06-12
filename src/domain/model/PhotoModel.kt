package io.photos.domain.model

import java.util.*

data class PhotoModel(
    val id: Long,
    val author: UserMetadataModel,
    val url: String,
    val metadata: PhotoMetadataModel
): Model

data class PhotoMetadataModel(val createdAt: Date): Model