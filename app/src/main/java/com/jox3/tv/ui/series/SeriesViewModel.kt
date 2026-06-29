package com.jox3.tv.ui.series

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jox3.tv.data.repository.SeriesRepository
import com.jox3.tv.data.repository.SettingsRepository
import com.jox3.tv.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SeriesUiState(
    val isLoading: Boolean = true,
    val categories: List<Category> = emptyList(),
    val series: List<Series> = emptyList(),
    val filteredSeries: List<Series> = emptyList(),
    val selectedCategoryId: String? = null,
    val searchQuery: String = "",
    val serverConfig: ServerConfig? = null,
    val error: String? = null
)

@HiltViewModel
class SeriesViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val seriesRepository: SeriesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeriesUiState())
    val uiState: StateFlow<SeriesUiState> = _uiState.asStateFlow()

    private var currentAccount: ServerConfig? = null

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            settingsRepository.getActiveAccount().collect { account ->
                if (account == null) {
                    _uiState.value = SeriesUiState(isLoading = false)
                    return@collect
                }
                currentAccount = account
                _uiState.value = _uiState.value.copy(serverConfig = account)

                launch {
                    seriesRepository.getCategories(account.id).collect { categories ->
                        _uiState.value = _uiState.value.copy(categories = categories)
                    }
                }
                launch {
                    seriesRepository.getSeries(account.id).collect { series ->
                        _uiState.value = _uiState.value.copy(
                            series = series,
                            filteredSeries = series,
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

    private fun applyFilters() {
        val state = _uiState.value
        val filtered = state.series.filter { s ->
            val matchesCategory = state.selectedCategoryId == null || s.categoryId == state.selectedCategoryId
            val matchesSearch = state.searchQuery.isEmpty() || s.name.contains(state.searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
        _uiState.value = _uiState.value.copy(filteredSeries = filtered)
    }

    fun getEpisodeStreamUrl(episodeId: String, extension: String?): String {
        val config = currentAccount ?: return ""
        return seriesRepository.getEpisodeStreamUrl(config, episodeId, extension)
    }
}
