package com.example.treinopago.services

import com.example.treinopago.services.dtos.ClientResponse
import com.example.treinopago.services.dtos.CreateClientRequest
import com.example.treinopago.services.dtos.CreatePlanRequest
import com.example.treinopago.services.dtos.PlanResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("client")
    suspend fun getAllClients(): Response<List<ClientResponse>>

    @GET("clients/{id}")
    suspend fun getClientById(@Path("id") clientId: String): Response<ClientResponse>

    @POST("client")
    suspend fun createClient(@Body clientData: CreateClientRequest): Response<ClientResponse>

    @PUT("clients/{id}")
    suspend fun updateClient(
        @Path("id") clientId: String,
        @Body clientData: CreateClientRequest
    ): Response<ClientResponse>

    @DELETE("clients/{id}")
    suspend fun deleteClient(@Path("id") clientId: String): Response<Unit>

    @GET("plan")
    suspend fun getAllPlans(): Response<List<PlanResponse>>

    @POST("plan")
    suspend fun createPlan(@Body planData: CreatePlanRequest): Response<PlanResponse>
}