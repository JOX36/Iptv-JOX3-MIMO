package com.jox3.tv.ui.movies

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.jox3.tv.ui.components.*
import com.jox3.tv.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(
    navController: NavController,
    viewModel: MoviesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        TopAppBar(
            title = { Text("Películas", color = TextPrimary) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
        )

        Jox3SearchBar(
            query = uiState.searchQuery,
            onQueryChange = { viewModel.updateSearch(it) },
            placeholder = "Buscar películas...",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        CategoryChips(
            categories = uiState.categories,
            selectedId = uiState.selectedCategoryId,
            onCategorySelected = { viewModel.selectCategory(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.isLoading) {
            LoadingIndicator()
        } else if (uiState.filteredMovies.isEmpty()) {
            EmptyState(message = "No se encontraron películas")
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.filteredMovies) { movie ->
                    MovieCard(
                        name = movie.name,
                        icon = movie.icon,
                        rating = movie.rating,
                        isFavorite = movie.isFavorite,
                        onClick = {
                            navController.navigate("movie_detail/${movie.streamId}")
                        },
                        onFavoriteClick = {}
                    )
                }
            }
        }
    }
}
