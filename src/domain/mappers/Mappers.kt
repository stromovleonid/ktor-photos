package io.photos.domain.mappers

import io.photos.domain.entities.UserMetadataEntity
import io.photos.domain.entities.UsernameEntity
import io.photos.domain.model.UserMetadataModel
import io.photos.domain.model.UsernameModel

class UserMetadataMapper(private val usernameMapper: Mapper<UsernameEntity, UsernameModel>): Mapper<UserMetadataEntity, UserMetadataModel> {
    override fun toModel(entity: UserMetadataEntity) = UserMetadataModel(entity.id, usernameMapper.toModel(entity.username))

    override fun toEntity(model: UserMetadataModel) = UserMetadataEntity(model.id, usernameMapper.toEntity(model.username))
}

class UsernameMapper: Mapper<UsernameEntity, UsernameModel> {
    override fun toModel(entity: UsernameEntity) = UsernameModel(entity.username)

    override fun toEntity(model: UsernameModel) = UsernameEntity(model.username)
}

