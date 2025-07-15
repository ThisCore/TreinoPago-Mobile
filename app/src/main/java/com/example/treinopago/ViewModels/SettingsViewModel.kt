package com.example.treinopago.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.treinopago.services.ApiService
import com.example.treinopago.services.RetrofitInstance
import com.example.treinopago.services.dtos.UpdatePixKeyRequest
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val apiService: ApiService = RetrofitInstance.api

    private val _darkModeEnabled = MutableLiveData<Boolean>()
    val darkModeEnabled: LiveData<Boolean> = _darkModeEnabled

    private val _pixKey = MutableLiveData<String?>(null)
    val pixKey: LiveData<String?> = _pixKey

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _updateSuccess = MutableLiveData<Boolean>(false)
    val updateSuccess: LiveData<Boolean> = _updateSuccess

    fun setDarkMode(enabled: Boolean) {
        _darkModeEnabled.value = enabled
    }

    fun fetchPixKey() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.getPixKey()
                if (response.isSuccessful) {
                    _pixKey.value = response.body()?.pixKey
                } else {
                    _error.value = "Erro ao buscar chave Pix: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Falha na conexão ao buscar chave Pix: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updatePixKey(newPixKey: String) {
        if (newPixKey.isBlank()) {
            _error.value = "Chave Pix não pode estar vazia"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _updateSuccess.value = false
            try {
                val request = UpdatePixKeyRequest(pixKey = newPixKey.trim())
                val response = apiService.updateUserPixKey(request)
                if (response.isSuccessful) {
                    _pixKey.value = newPixKey.trim()
                    _updateSuccess.value = true
                } else {
                    _error.value = "Erro ao atualizar chave Pix: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Falha na conexão ao atualizar chave Pix: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearUpdateSuccess() {
        _updateSuccess.value = false
    }

    fun validatePixKey(pixKey: String): Boolean {
        val trimmedKey = pixKey.trim()
        if (trimmedKey.isBlank()) return false

        // Validação básica para diferentes tipos de chave Pix
        return when {
            // CPF (11 dígitos)
            trimmedKey.matches(Regex("^\\d{11}$")) -> true
            // CNPJ (14 dígitos)
            trimmedKey.matches(Regex("^\\d{14}$")) -> true
            // Email
            trimmedKey.matches(Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) -> true
            // Telefone (+5511999999999)
            trimmedKey.matches(Regex("^\\+55\\d{10,11}$")) -> true
            // Chave aleatória (UUID)
            trimmedKey.matches(Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")) -> true
            else -> false
        }
    }
}
