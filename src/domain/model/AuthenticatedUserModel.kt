package io.photos.domain.model

import com.google.gson.annotations.SerializedName

data class AuthenticatedUserModel(val token: String,
                                  @SerializedName("metadata")
                                  val metadataModel: UserMetadataModel): Model