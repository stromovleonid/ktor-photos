package io.photos.domain.mappers

import io.photos.domain.entities.UserMetadataEntity
import io.photos.domain.entities.UsernameEntity
import io.photos.domain.model.UserMetadataModel

class UserMetadataMapper: Mapper<UserMetadataEntity, UserMetadataModel> {
    override fun toModel(entity: UserMetadataEntity) = UserMetadataModel(entity.id, entity.username.username)

    override fun toEntity(model: UserMetadataModel) = UserMetadataEntity(model.id, UsernameEntity(model.username))
}
