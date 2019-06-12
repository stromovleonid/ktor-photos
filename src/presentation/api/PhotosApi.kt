package io.photos.presentation.api

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.photos.domain.usecases.photos.PhotosUseCase
import io.photos.domain.utils.getApiResponseCode
import io.photos.domain.utils.toApiResponse
import org.koin.core.KoinComponent
import org.koin.core.inject

object PhotosApi : KoinComponent {
    private val useCase by inject<PhotosUseCase>()

    fun Route.photos() {
        get("/photos") {
            val result = useCase.feed(
                call.request.queryParameters["pageIndex"],
                call.request.queryParameters["pageSize"]
            )
            call.respond(result.getApiResponseCode(), result.toApiResponse())
        }

        get("/photos/{userId}") {
            val result = useCase.photosOfUser(
                call.parameters["userId"],
                call.request.queryParameters["pageIndex"],
                call.request.queryParameters["pageSize"]
            )
            call.respond(result.getApiResponseCode(), result.toApiResponse())
        }
    }
}