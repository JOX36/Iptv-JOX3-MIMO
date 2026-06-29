package com.jox3.tv.ui.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jox3.tv.data.repository.MovieRepository
import com.jox3.tv.data.repository.SettingsRepository
import com.jox3.tv.domain.model.Category
import com.jox3.tv.domain.model.Movie
import com.jox3.tv.domain.model.ServerConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MoviesUiState(
    val isLoading: Boolean = true,
    val categories: List<Category> = emptyList(),
    val movies: List<Movie> = emptyList(),
    val filteredMovies: List<Movie> = emptyList(),
    val selectedCategoryId: String? = null,
    val searchQuery: String = "",
    val serverConfig: ServerConfig? = null,
    val error: String? = null
)

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoviesUiState())
    val uiState: StateFlow<MoviesUiState> = _uiState.asStateFlow()

    private var currentAccount: ServerConfig? = null

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            settingsRepository.getActiveAccount().collect { account ->
                if (account == null) {
                    _uiState.value = MoviesUiState(isLoading = false)
                    return@collect
                }
                currentAccount = account
                _uiState.value = _uiState.value.copy(serverConfig = account)

                launch {
                    movieRepository.getCategories(account.id).collect { categories ->
                        _uiState.value = _uiState.value.copy(categories = categories)
                    }
                }
                launch {
                    movieRepository.getMovies(account.id).collect { movies ->
                        _uiState.value = _uiState.value.copy(
                            movies = movies,
                            filteredMovies = movies,
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
        val filtered = state.movies.filter { movie ->
            val matchesCategory = state.selectedCategoryId == null || movie.categoryId == state.selectedCategoryId
            val matchesSearch = state.searchQuery.isEmpty() || movie.name.contains(state.searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
        _uiState.value = _uiState.value.copy(filteredMovies = filtered)
    }

    fun getStreamUrl(streamId: Int, extension: String?): String {
        val config = currentAccount ?: return ""
        return movieRepository.getStreamUrl(config, streamId, extension)
    }
}
