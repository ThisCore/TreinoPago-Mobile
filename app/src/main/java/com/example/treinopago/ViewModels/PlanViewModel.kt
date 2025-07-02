package com.example.treinopago.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.treinopago.services.dtos.PlanResponse
import com.example.treinopago.services.RetrofitInstance
import com.example.treinopago.services.dtos.CreatePlanRequest
import com.example.treinopago.ui.screens.BillingFrequency
import kotlinx.coroutines.launch
import java.util.Collections.frequency

class PlanViewModel : ViewModel() {

    private val _plans = MutableLiveData<List<PlanResponse>>()
    val plans: LiveData<List<PlanResponse>> = _plans

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _planCreationSuccess = MutableLiveData<Boolean>()
    val planCreationSuccess: LiveData<Boolean> = _planCreationSuccess

    fun fetchAllPlans() {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getAllPlans()
                if (response.isSuccessful) {
                    _plans.value = response.body()
                    if (response.body().isNullOrEmpty()){
                        println("Nenhum plano retornado pela API, mas a chamada foi bem-sucedida.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _error.value = "Erro ${response.code()}: ${errorBody ?: response.message()}"
                    println("Erro API Planos: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                _error.value = "Falha na conexão ao buscar planos: ${e.message}"
                println("Exceção de rede Planos: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createNewPlan(
        name: String,
        description: String?,
        price: Double,
        recurrence: BillingFrequency,

    ) {
        _isLoading.value = true
        _error.value = null
        _planCreationSuccess.value = false
        viewModelScope.launch {
            try {
                val request = CreatePlanRequest(
                    name = name,
                    description = description,
                    price = price,
                    recurrence = recurrence,
                )
                val response = RetrofitInstance.api.createPlan(request)
                if (response.isSuccessful) {
                    _planCreationSuccess.value = true
                    fetchAllPlans()
                    println("Plano criado via API: ${response.body()}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    _error.value = "Erro ao criar plano ${response.code()}: ${errorBody ?: response.message()}"
                    println("Erro API Planos (Criação): ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                _error.value = "Falha na conexão ao criar plano: ${e.message}"
                println("Exceção de rede Planos (Criação): ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetPlanCreationStatus() {
        _planCreationSuccess.value = false
    }

    fun clearError() {
        _error.value = null
    }

}