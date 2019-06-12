package io.photos.presentation.api

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.principal
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.photos.domain.usecases.photos.PhotosUseCase
import io.photos.domain.utils.Either
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

        get("/photos/download/{fileName}") {
            val path = call.parameters["fileName"]
            when (val imageResult = useCase.getImage(path)) {
                is Either.Success -> call.respondFile(imageResult.result)
                is Either.Failure -> call.respond(imageResult.error.apiCode, imageResult.error.apiMessage)
            }
        }

        authenticate {
            post("/photos/upload") {
                val multipart = call.receiveMultipart()
                val userId = call.principal<JWTPrincipal>()?.payload?.claims?.get("id")
                val photo = useCase.uploadPhoto(userId?.asLong(), multipart)
                call.respond(photo.getApiResponseCode(), photo.toApiResponse())
            }
        }
    }
}