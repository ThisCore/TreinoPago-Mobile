package com.example.treinopago.services

import com.example.treinopago.services.dtos.ClientResponse
import com.example.treinopago.services.dtos.CreateClientRequest
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Exemplo: GET para obter todos os clientes
    @GET("client")
    suspend fun getAllClients(): Response<List<ClientResponse>>

    @GET("clients/{id}")
    suspend fun getClientById(@Path("id") clientId: String): Response<ClientResponse>

    @POST("clients")
    suspend fun createClient(@Body clientData: CreateClientRequest): Response<ClientResponse>

    @PUT("clients/{id}")
    suspend fun updateClient(
        @Path("id") clientId: String,
        @Body clientData: CreateClientRequest
    ): Response<ClientResponse>

    @DELETE("clients/{id}")
    suspend fun deleteClient(@Path("id") clientId: String): Response<Unit> // Unit se não houver corpo na resposta

    // Adicione outros endpoints conforme necessário (planos, cobranças, etc.)
    // @GET("plans")
    // suspend fun getAllPlans(): Response<List<PlanResponse>>

    // @POST("plans")
    // suspend fun createPlan(@Body planData: CreatePlanRequest): Response<PlanResponse>
}