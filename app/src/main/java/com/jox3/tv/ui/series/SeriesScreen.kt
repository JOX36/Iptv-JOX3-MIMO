package com.jox3.tv.ui.series

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.jox3.tv.ui.components.*
import com.jox3.tv.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesScreen(
    navController: NavController,
    viewModel: SeriesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        TopAppBar(
            title = { Text("Series", color = TextPrimary) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
        )

        Jox3SearchBar(
            query = uiState.searchQuery,
            onQueryChange = { viewModel.updateSearch(it) },
            placeholder = "Buscar series...",
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
        } else if (uiState.filteredSeries.isEmpty()) {
            EmptyState(message = "No se encontraron series")
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.filteredSeries) { series ->
                    SeriesCard(
                        name = series.name,
                        cover = series.cover,
                        rating = series.rating,
                        isFavorite = series.isFavorite,
                        onClick = {
                            navController.navigate("series_detail/${series.seriesId}")
                        },
                        onFavoriteClick = {}
                    )
                }
            }
        }
    }
}
