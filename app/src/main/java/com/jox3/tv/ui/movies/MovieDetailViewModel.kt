package com.jox3.tv.ui.movies

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jox3.tv.data.repository.MovieRepository
import com.jox3.tv.data.repository.SettingsRepository
import com.jox3.tv.domain.model.MovieDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MovieDetailUiState(
    val isLoading: Boolean = true,
    val movie: MovieDetail? = null,
    val streamUrl: String? = null,
    val error: String? = null
)

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val movieRepository: MovieRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val vodId: Int = savedStateHandle.get<Int>("vodId") ?: 0
    private val _uiState = MutableStateFlow(MovieDetailUiState())
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    init { loadMovie() }

    private fun loadMovie() {
        viewModelScope.launch {
            val account = settingsRepository.getActiveAccountSync() ?: return@launch
            try {
                val detail = movieRepository.getMovieDetail(account, vodId)
                val ext = detail.backdropPaths.firstOrNull() ?: "mp4"
                val url = "${account.movieBaseUrl}$vodId.$ext"
                _uiState.value = MovieDetailUiState(movie = detail, streamUrl = url)
            } catch (e: Exception) {
                _uiState.value = MovieDetailUiState(error = e.message)
            }
        }
    }
}
