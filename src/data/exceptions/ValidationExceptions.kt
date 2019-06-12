package data.exceptions

import io.photos.domain.requests.RequestParams
import java.lang.IllegalArgumentException

open class ValidationException: IllegalArgumentException()

class InvalidUsernameException(username: String): ValidationException() {
    override val message = "Invalid username $username"
}

class InvalidUserIdException(id: Long): ValidationException() {
    override val message = "Invalid user id $id"
}

class InvalidParamsException(params: RequestParams, cause: String? = null): ValidationException() {
    override val message = "Invalid params \"$params\". " + (cause ?: "")
}
