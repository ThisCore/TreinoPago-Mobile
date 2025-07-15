package com.example.treinopago.services

import com.example.treinopago.services.dtos.BillingDTO
import com.example.treinopago.services.dtos.ClientResponse
import com.example.treinopago.services.dtos.CreateClientRequest
import com.example.treinopago.services.dtos.CreatePlanRequest
import com.example.treinopago.services.dtos.PixKeyResponse
import com.example.treinopago.services.dtos.PlanResponse
import com.example.treinopago.services.dtos.UpdateClientRequest
import com.example.treinopago.services.dtos.UpdatePixKeyRequest
import com.example.treinopago.services.dtos.UpdatePlanRequest
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("client")
    suspend fun getAllClients(): Response<List<ClientResponse>>

    @GET("client/{id}")
    suspend fun getClientById(@Path("id") clientId: String): Response<ClientResponse>

    @POST("client")
    suspend fun createClient(@Body clientData: CreateClientRequest): Response<ClientResponse>

    @PATCH("client/{id}")
    suspend fun updateClient(
        @Path("id") clientId: String,
        @Body clientData: UpdateClientRequest
    ): Response<ClientResponse>

    @DELETE("client/{id}")
    suspend fun deleteClient(@Path("id") clientId: String): Response<Unit>

    @GET("plan")
    suspend fun getAllPlans(): Response<List<PlanResponse>>

    @POST("plan")
    suspend fun createPlan(@Body planData: CreatePlanRequest): Response<PlanResponse>

    @GET("plan/{id}")
    suspend fun getPlanById(@Path("id") planId: String): Response<PlanResponse>

    @PATCH("plan/{id}")
    suspend fun updatePlan(
        @Path("id") planId: String,
        @Body planData: UpdatePlanRequest
    ): Response<PlanResponse>

    @DELETE("plan/{id}")
    suspend fun deletePlan(@Path("id") planId: String): Response<Unit>

    @GET("charge")
    suspend fun getAllBillings(): Response<List<BillingDTO>>

    @GET("charge/{id}")
    suspend fun getBillingById(@Path("id") billingId: String): Response<BillingDTO>

    @POST("system-config")
    suspend fun updateUserPixKey(@Body request: UpdatePixKeyRequest): Response<Unit>

    @GET("system-config")
    suspend fun getPixKey(): Response<PixKeyResponse>
}
