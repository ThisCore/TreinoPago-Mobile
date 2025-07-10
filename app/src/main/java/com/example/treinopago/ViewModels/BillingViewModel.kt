package com.example.treinopago.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.treinopago.services.ApiService
import com.example.treinopago.services.RetrofitInstance
import com.example.treinopago.services.dtos.BillingDTO
import kotlinx.coroutines.launch

class BillingViewModel : ViewModel() {

    private val apiService: ApiService = RetrofitInstance.api

    private val _billings = MutableLiveData<List<BillingDTO>>(emptyList())
    val billings: LiveData<List<BillingDTO>> = _billings

    private val _selectedBilling = MutableLiveData<BillingDTO?>()
    val selectedBilling: LiveData<BillingDTO?> = _selectedBilling

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun fetchAllBillings() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.getAllBillings()
                if (response.isSuccessful) {
                    _billings.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Erro ao buscar cobranças: ${response.code()} - ${response.message()}"
                    _billings.value = emptyList()
                }
            } catch (e: Exception) {
                _error.value = "Falha na conexão ao buscar cobranças: ${e.message}"
                _billings.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchBillingById(billingId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _selectedBilling.value = null
            try {
                val response = apiService.getBillingById(billingId)
                if (response.isSuccessful) {
                    _selectedBilling.value = response.body()
                } else {
                    _error.value = "Erro ao buscar detalhes da cobrança: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Falha na conexão ao buscar detalhes da cobrança: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSelectedBilling() {
        _selectedBilling.value = null
    }
}
