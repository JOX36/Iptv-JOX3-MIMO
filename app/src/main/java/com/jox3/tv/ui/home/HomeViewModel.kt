package com.jox3.tv.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jox3.tv.data.repository.EpgRepository
import com.jox3.tv.data.repository.LiveRepository
import com.jox3.tv.data.repository.MovieRepository
import com.jox3.tv.data.repository.SettingsRepository
import com.jox3.tv.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val hasAccount: Boolean = false,
    val favoriteChannels: List<LiveChannel> = emptyList(),
    val recentChannels: List<LiveChannel> = emptyList(),
    val recentMovies: List<Movie> = emptyList(),
    val currentPrograms: Map<String, EpgProgram> = emptyMap(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val liveRepository: LiveRepository,
    private val movieRepository: MovieRepository,
    private val epgRepository: EpgRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            settingsRepository.getActiveAccount().collect { account ->
                if (account == null) {
                    _uiState.value = HomeUiState(isLoading = false, hasAccount = false)
                    return@collect
                }

                _uiState.value = _uiState.value.copy(hasAccount = true)

                // Load favorites and recent
                launch {
                    liveRepository.getFavorites(account.id).collect { favorites ->
                        _uiState.value = _uiState.value.copy(favoriteChannels = favorites)
                    }
                }
                launch {
                    liveRepository.getRecentlyWatched(account.id).collect { recent ->
                        _uiState.value = _uiState.value.copy(recentChannels = recent)
                    }
                }
                launch {
                    movieRepository.getRecentlyWatched(account.id).collect { recent ->
                        _uiState.value = _uiState.value.copy(recentMovies = recent)
                    }
                }

                // Load EPG for favorites
                launch {
                    try {
                        val epg = epgRepository.getFullEpg(account)
                        val currentPrograms = mutableMapOf<String, EpgProgram>()
                        val now = System.currentTimeMillis()
                        epg.forEach { (channelId, programs) ->
                            programs.find { now in it.startTime..it.endTime }?.let {
                                currentPrograms[channelId] = it
                            }
                        }
                        _uiState.value = _uiState.value.copy(
                            currentPrograms = currentPrograms,
                            isLoading = false
                        )
                    } catch (_: Exception) {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    }
                }
            }
        }
    }

    fun toggleFavorite(streamId: Int) {
        viewModelScope.launch {
            val account = settingsRepository.getActiveAccountSync() ?: return@launch
            liveRepository.toggleFavorite(streamId, account.id)
        }
    }
}
