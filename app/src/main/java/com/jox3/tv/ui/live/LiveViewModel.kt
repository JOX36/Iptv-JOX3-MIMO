package com.jox3.tv.ui.live

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jox3.tv.data.repository.LiveRepository
import com.jox3.tv.data.repository.SettingsRepository
import com.jox3.tv.domain.model.Category
import com.jox3.tv.domain.model.EpgProgram
import com.jox3.tv.domain.model.LiveChannel
import com.jox3.tv.domain.model.ServerConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LiveUiState(
    val isLoading: Boolean = true,
    val categories: List<Category> = emptyList(),
    val channels: List<LiveChannel> = emptyList(),
    val filteredChannels: List<LiveChannel> = emptyList(),
    val selectedCategoryId: String? = null,
    val searchQuery: String = "",
    val currentPrograms: Map<String, EpgProgram> = emptyMap(),
    val serverConfig: ServerConfig? = null,
    val error: String? = null
)

@HiltViewModel
class LiveViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val liveRepository: LiveRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LiveUiState())
    val uiState: StateFlow<LiveUiState> = _uiState.asStateFlow()

    private var currentAccount: com.jox3.tv.domain.model.ServerConfig? = null

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            settingsRepository.getActiveAccount().collect { account ->
                if (account == null) {
                    _uiState.value = LiveUiState(isLoading = false)
                    return@collect
                }
                currentAccount = account
                _uiState.value = _uiState.value.copy(serverConfig = account)

                launch {
                    liveRepository.getCategories(account.id).collect { categories ->
                        _uiState.value = _uiState.value.copy(categories = categories)
                    }
                }
                launch {
                    liveRepository.getChannels(account.id).collect { channels ->
                        _uiState.value = _uiState.value.copy(
                            channels = channels,
                            filteredChannels = channels,
                            isLoading = false
                        )
                        applyFilters()
                    }
                }
            }
        }
    }

    fun selectCategory(categoryId: String?) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
        applyFilters()
    }

    fun updateSearch(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun toggleFavorite(streamId: Int) {
        viewModelScope.launch {
            val account = currentAccount ?: return@launch
            liveRepository.toggleFavorite(streamId, account.id)
        }
    }

    private fun applyFilters() {
        val state = _uiState.value
        val filtered = state.channels.filter { channel ->
            val matchesCategory = state.selectedCategoryId == null || channel.categoryId == state.selectedCategoryId
            val matchesSearch = state.searchQuery.isEmpty() || channel.name.contains(state.searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
        _uiState.value = _uiState.value.copy(filteredChannels = filtered)
    }
}
