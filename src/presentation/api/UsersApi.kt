package io.photos.presentation.api

import domain.usecases.users.metadata.UserMetadataUseCase
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.photos.domain.utils.getApiResponseCode
import io.photos.domain.utils.toApiResponse
import org.koin.core.KoinComponent
import org.koin.core.inject

object UsersApi: KoinComponent {

    private val useCase by inject<UserMetadataUseCase>()

    fun Routing.users() {
        get("/users/{id}") {
            val result = useCase.findById(call.parameters["id"])
            call.respond(result.getApiResponseCode(), result.toApiResponse())
        }

        get("/users") {
            val result = useCase.findAll(
                call.request.queryParameters["query"],
                call.request.queryParameters["ignoreCase"],
                call.request.queryParameters["pageIndex"],
                call.request.queryParameters["pageSize"]
            )
            call.respond(result.getApiResponseCode(), result.toApiResponse())
        }
    }

}
