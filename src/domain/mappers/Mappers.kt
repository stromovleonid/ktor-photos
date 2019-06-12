package io.photos.domain.mappers

import io.photos.domain.entities.*
import io.photos.domain.model.PhotoMetadataModel
import io.photos.domain.model.PhotoModel
import io.photos.domain.model.UserMetadataModel

class UserMetadataMapper : Mapper<UserMetadataEntity, UserMetadataModel> {
    override fun toModel(entity: UserMetadataEntity) =
        UserMetadataModel(
            entity.id.id,
            entity.username.username,
            entity.createdAt,
            entity.avatar.url
        )

    override fun toEntity(model: UserMetadataModel) =
        UserMetadataEntity(
            UserIdEntity(model.id),
            UsernameEntity(model.username),
            model.createdAt,
            AvatarEntity(model.avatarUrl)
        )
}

interface PhotosToModelMapper {
    fun toModel(entity: PhotoEntity, param: UserMetadataModel, imageHost: String): PhotoModel
}

class PhotosToModelMapperImpl : PhotosToModelMapper {
    private val externalUrlRegex =
        Regex("(https?://(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?://(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})\n")

    override fun toModel(entity: PhotoEntity, param: UserMetadataModel, imageHost: String) =
        PhotoModel(
            entity.id.id,
            param,
            if (entity.url.isExternalUrl()) entity.url else (imageHost + entity.url),
            PhotoMetadataModel(entity.metadata.createdAt)
        )

    private fun String.isExternalUrl() = externalUrlRegex.matches(this)
}
