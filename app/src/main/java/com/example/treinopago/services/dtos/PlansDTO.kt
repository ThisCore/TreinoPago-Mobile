package com.example.treinopago.services.dtos

import com.example.treinopago.ui.screens.BillingFrequency
import com.google.gson.annotations.SerializedName

data class PlanResponse(
    val id: String,
    val name: String,
    val description: String?,
    val price: Double,
    @SerializedName("duration_days")
    val durationDays: Int?,
    val durationDescription: String?,
    @SerializedName("is_active")
    val isActive: Boolean
)

data class CreatePlanRequest(
    val name: String,
    val description: String?,
    val price: Double,
    val recurrence: BillingFrequency,
)