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

class PhotosToModelMapper : ParametrizedToModelMapper<PhotoEntity, PhotoModel, UserMetadataModel> {
    override fun toModel(entity: PhotoEntity, param: UserMetadataModel) =
        PhotoModel(
            entity.id.id,
            param,
            entity.url,
            PhotoMetadataModel(entity.metadata.width, entity.metadata.height, entity.metadata.createdAt)
        )

}
