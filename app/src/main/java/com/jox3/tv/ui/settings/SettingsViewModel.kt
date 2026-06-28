package com.jox3.tv.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jox3.tv.data.repository.SettingsRepository
import com.jox3.tv.domain.model.ServerConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val accounts: List<ServerConfig> = emptyList(),
    val hardwareDecoding: Boolean = true,
    val autoPlay: Boolean = true,
    val autoLoadEpg: Boolean = true,
    val epgCacheSize: Float = 0f
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.getAllAccounts().collect { accounts ->
                _uiState.value = _uiState.value.copy(accounts = accounts)
            }
        }
    }

    fun activateAccount(id: Long) {
        viewModelScope.launch { settingsRepository.activateAccount(id) }
    }

    fun deleteAccount(id: Long) {
        viewModelScope.launch { settingsRepository.deleteAccount(id) }
    }

    fun setHardwareDecoding(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(hardwareDecoding = enabled)
    }

    fun setAutoPlay(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(autoPlay = enabled)
    }

    fun setAutoLoadEpg(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(autoLoadEpg = enabled)
    }

    fun clearEpgCache() {
        _uiState.value = _uiState.value.copy(epgCacheSize = 0f)
    }
}
