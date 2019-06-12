package io.photos

import com.google.gson.internal.bind.DateTypeAdapter
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.authenticate
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.photos.presentation.api.AuthApi.auth
import io.photos.presentation.api.AuthApi.initAuth
import io.photos.presentation.api.PhotosApi.photos
import io.photos.presentation.api.UsersApi.users
import io.photos.presentation.di.KoinContainer
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    KoinContainer.init("http://0.0.0.0:8080/photos/download")

    install(ContentNegotiation) {
        gson {
            registerTypeAdapter(Date::class.java, DateTypeAdapter())
        }
    }

    initAuth()

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        auth()
        users()
        photos()
    }
}
