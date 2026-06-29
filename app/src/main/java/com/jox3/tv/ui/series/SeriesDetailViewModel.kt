package com.jox3.tv.ui.series

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jox3.tv.data.repository.SeriesRepository
import com.jox3.tv.data.repository.SettingsRepository
import com.jox3.tv.domain.model.SeriesDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SeriesDetailUiState(
    val isLoading: Boolean = true,
    val series: SeriesDetail? = null,
    val selectedSeason: Int = 1,
    val seriesBaseUrl: String = "",
    val error: String? = null
)

@HiltViewModel
class SeriesDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val seriesRepository: SeriesRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val seriesId: Int = savedStateHandle.get<Int>("seriesId") ?: 0
    private val _uiState = MutableStateFlow(SeriesDetailUiState())
    val uiState: StateFlow<SeriesDetailUiState> = _uiState.asStateFlow()

    init { loadSeries() }

    private fun loadSeries() {
        viewModelScope.launch {
            val account = settingsRepository.getActiveAccountSync() ?: return@launch
            try {
                val detail = seriesRepository.getSeriesDetail(account, seriesId)
                if (detail != null) {
                    val firstSeason = detail.seasons.firstOrNull()?.seasonNumber ?: 1
                    _uiState.value = SeriesDetailUiState(series = detail, selectedSeason = firstSeason, seriesBaseUrl = account.seriesBaseUrl)
                } else {
                    _uiState.value = SeriesDetailUiState(error = "Serie no encontrada")
                }
            } catch (e: Exception) {
                _uiState.value = SeriesDetailUiState(error = e.message)
            }
        }
    }

    fun selectSeason(season: Int) {
        _uiState.value = _uiState.value.copy(selectedSeason = season)
    }
}
