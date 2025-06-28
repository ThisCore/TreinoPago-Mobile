package com.example.treinopago.services.dtos

data class ClientResponse(
    val id: String,
    val name: String,
    val email: String,
    val startDate: Long
)

data class CreateClientRequest(
    val name: String,
    val email: String,
    val startDate: Long
)