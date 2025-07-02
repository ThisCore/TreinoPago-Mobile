package com.example.treinopago.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.treinopago.services.RetrofitInstance
import com.example.treinopago.services.dtos.ClientResponse
import com.example.treinopago.services.dtos.CreateClientRequest
import com.example.treinopago.services.dtos.PlanResponse
import com.example.treinopago.services.dtos.UpdateClientRequest
import kotlinx.coroutines.launch

class ClientViewModel : ViewModel() {

    private val _clients = MutableLiveData<List<ClientResponse>>()
    val clients: LiveData<List<ClientResponse>> = _clients

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _creationSuccess = MutableLiveData<Boolean>()
    val creationSuccess: LiveData<Boolean> = _creationSuccess

    private val _clientUpdateSuccess = MutableLiveData<Boolean>()
    val clientUpdateSuccess: LiveData<Boolean> = _clientUpdateSuccess

    private val _clientDeletionSuccess = MutableLiveData<Boolean>()
    val clientDeletionSuccess: LiveData<Boolean> = _clientDeletionSuccess

    private val _selectedClient = MutableLiveData<ClientResponse?>()
    val selectedClient: LiveData<ClientResponse?> = _selectedClient

    private val _availablePlans = MutableLiveData<List<PlanResponse>>()
    val availablePlans: LiveData<List<PlanResponse>> = _availablePlans


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

    fun createNewClient(name: String, email: String, billingStartDate: Long, planId: String) {
        _isLoading.value = true
        _error.value = null
        _creationSuccess.value = false
        viewModelScope.launch {
            try {
                val request = CreateClientRequest(name, email, billingStartDate , planId)
                val response = RetrofitInstance.api.createClient(request)
                if (response.isSuccessful) {
                    _creationSuccess.value = true
                } else {
                    val errorBody = response.errorBody()?.string()
                    val specificErrorMessage = if (!errorBody.isNullOrBlank()) {
                        errorBody
                    } else {
                        response.message()
                    }
                    _error.value = "Erro ${response.code()}: $specificErrorMessage"
                    println("Erro API: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                _error.value = "Falha na conexão: ${e.message}"
                println("Exceção de rede: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun fetchClientById(clientId: String) {
        _isLoading.value = true
        _error.value = null
        _selectedClient.value = null
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getClientById(clientId)
                if (response.isSuccessful) {
                    _selectedClient.value = response.body()
                } else {
                    _error.value = "Erro ao buscar cliente ${response.code()}: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Falha na conexão ao buscar cliente: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun updateExistingClient(
        clientId: String,
        name: String?,
        email: String?,
        planId: String?
    ) {
        _isLoading.value = true
        _error.value = null
        _clientUpdateSuccess.value = false
        viewModelScope.launch {
            try {
                val request = UpdateClientRequest(
                    name = name,
                    email = email,
                    planId = planId
                )
                val response = RetrofitInstance.api.updateClient(clientId, request)
                if (response.isSuccessful) {
                    _clientUpdateSuccess.value = true
                    _selectedClient.value = response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    _error.value = "Erro ao atualizar cliente ${response.code()}: ${errorBody ?: response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Falha na conexão ao atualizar cliente: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteClient(clientId: String) {
        _isLoading.value = true
        _error.value = null
        _clientDeletionSuccess.value = false
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.deleteClient(clientId)
                if (response.isSuccessful) {
                    _clientDeletionSuccess.value = true
                    if (_selectedClient.value?.id == clientId) {
                        _selectedClient.value = null
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _error.value = "Erro ao excluir cliente ${response.code()}: ${errorBody ?: response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Falha na conexão ao excluir cliente: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetClientUpdateStatus() {
        _clientUpdateSuccess.value = false
    }

    fun resetClientDeletionStatus() {
        _clientDeletionSuccess.value = false
    }

    fun clearSelectedClient() {
        _selectedClient.value = null
    }

    fun resetCreationStatus() {
        _creationSuccess.value = false
    }

    fun clearError() {
        _error.value = null
    }

}