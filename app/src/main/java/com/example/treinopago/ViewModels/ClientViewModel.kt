package com.example.treinopago.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.treinopago.services.RetrofitInstance
import com.example.treinopago.services.dtos.ClientResponse
import com.example.treinopago.services.dtos.CreateClientRequest
import kotlinx.coroutines.launch

class ClientViewModel : ViewModel() {

    private val _clients = MutableLiveData<List<ClientResponse>>()
    val clients: LiveData<List<ClientResponse>> = _clients

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun fetchAllClients() {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getAllClients()
                if (response.isSuccessful) {
                    _clients.value = response.body()
                } else {
                    _error.value = "Erro ao buscar clientes: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Falha na conexão: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createNewClient(name: String, email: String, startDate: Long) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val request = CreateClientRequest(name, email, startDate)
                val response = RetrofitInstance.api.createClient(request)
                if (response.isSuccessful) {
                    fetchAllClients()
                    println("Cliente criado: ${response.body()}")
                } else {
                    _error.value = "Erro ao criar cliente: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Falha ao criar cliente: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

}