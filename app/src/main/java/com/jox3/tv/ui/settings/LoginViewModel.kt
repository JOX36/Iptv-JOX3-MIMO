package com.jox3.tv.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jox3.tv.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val testResult: Boolean? = null,
    val loginSuccess: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(url: String, port: String, username: String, password: String, name: String) {
        if (url.isBlank() || username.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Completa todos los campos obligatorios")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            val result = settingsRepository.login(url, port, username, password)
            result.fold(
                onSuccess = { config ->
                    settingsRepository.activateAccount(config.id)
                    _uiState.value = LoginUiState(loginSuccess = true)
                },
                onFailure = { e ->
                    _uiState.value = LoginUiState(error = e.message ?: "Error desconocido")
                }
            )
        }
    }

    fun testConnection(url: String, port: String, username: String, password: String) {
        if (url.isBlank() || username.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Completa todos los campos", testResult = null)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, testResult = null, error = null)
            val result = settingsRepository.testConnection(url, port, username, password)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                testResult = result.isSuccess,
                error = result.exceptionOrNull()?.message
            )
        }
    }
}
