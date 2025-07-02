package com.example.treinopago.services.dtos

data class ClientResponse(
    val id: String,
    val name: String,
    val email: String,
    val startDate: Long,
    val planId: String
)

data class CreateClientRequest(
    val name: String,
    val email: String,
    val startDate: Long,
    val planId: String
)

data class UpdateClientRequest(
    val name: String?,
    val email: String?,
    val planId: String?
)