package io.photos.domain.exceptions

import java.lang.IllegalArgumentException

open class ValidationException: IllegalArgumentException()

class InvalidUsernameException(username: String): ValidationException() {
    override val message = "Invalid username $username"
}

class InvalidUserIdException(id: Long): ValidationException() {
    override val message = "Invalid user id $id"
}

