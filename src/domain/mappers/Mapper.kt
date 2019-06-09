package io.photos.domain.mappers

import io.photos.domain.entities.Entity
import io.photos.domain.model.Model

interface EntityToModelMapper<E: Entity, M: Model> {
    fun toModel(entity: E): M
}

interface ModelToEnityMapper<E: Entity, M: Model> {
    fun toEntity(model: M): E
}

interface Mapper<E: Entity, M: Model>: EntityToModelMapper<E, M>, ModelToEnityMapper<E, M>