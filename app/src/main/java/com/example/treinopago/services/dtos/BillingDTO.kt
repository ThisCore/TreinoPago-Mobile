package com.example.treinopago.services.dtos

import com.google.gson.annotations.SerializedName

data class BillingDTO(
    @SerializedName("id")
    val id: String,
    val clientId: String,
    val dueDate: String,
    val amount: Double,
    val status: String,
    val reminderSent: Boolean,
    val client: ClientInBillingDTO
)

data class ClientInBillingDTO(
    val id: String,
    val name: String,
    val email: String?,
    val paymentStatus: String,
    val billingStartDate: String,
    val planId: String,
    val plan: PlanInBillingDTO
)

data class PlanInBillingDTO(
    val id: String,
    val name: String,
    val price: Double,
    val recurrence: String
)