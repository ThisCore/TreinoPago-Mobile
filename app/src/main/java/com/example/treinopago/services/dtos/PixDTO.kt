package com.example.treinopago.services.dtos

import com.google.gson.annotations.SerializedName

data class UpdatePixKeyRequest(
    @SerializedName("pixKey")
    val pixKey: String
)

data class PixKeyResponse(
    @SerializedName("pixKey")
    val pixKey: String
)