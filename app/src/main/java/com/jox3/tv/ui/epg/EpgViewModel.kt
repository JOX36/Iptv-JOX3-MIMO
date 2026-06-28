package com.jox3.tv.ui.epg

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jox3.tv.data.repository.EpgRepository
import com.jox3.tv.data.repository.LiveRepository
import com.jox3.tv.data.repository.SettingsRepository
import com.jox3.tv.domain.model.EpgProgram
import com.jox3.tv.domain.model.LiveChannel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EpgUiState(
    val isLoading: Boolean = true,
    val channels: List<LiveChannel> = emptyList(),
    val epgData: Map<String, List<EpgProgram>> = emptyMap(),
    val selectedChannelId: String? = null,
    val error: String? = null
)

@HiltViewModel
class EpgViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val liveRepository: LiveRepository,
    private val epgRepository: EpgRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EpgUiState())
    val uiState: StateFlow<EpgUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val account = settingsRepository.getActiveAccountSync()
            if (account == null) {
                _uiState.value = EpgUiState(isLoading = false)
                return@launch
            }

            launch {
                liveRepository.getChannels(account.id).collect { channels ->
                    _uiState.value = _uiState.value.copy(channels = channels)
                }
            }

            launch {
                try {
                    val epg = epgRepository.getFullEpg(account)
                    _uiState.value = _uiState.value.copy(
                        epgData = epg,
                        isLoading = false
                    )
                } catch (_: Exception) {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }

    fun selectChannel(channelId: String?) {
        _uiState.value = _uiState.value.copy(selectedChannelId = channelId)
    }
}
