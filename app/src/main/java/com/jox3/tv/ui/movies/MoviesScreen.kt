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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    navController: NavController,
    viewModel: MovieDetailViewModel = hiltViewModel()
) {
    val navBackStackEntry = navController.currentBackStackEntry
    val vodId = navBackStackEntry?.arguments?.getInt("seriesId") ?: 0
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(vodId) {
        viewModel.loadDetail(vodId)
    }

    if (uiState.isLoading) {
        LoadingIndicator()
        return
    }

    val detail = uiState.detail

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Backdrop
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(SurfaceVariantDark)
            ) {
                if (detail?.backdropPaths?.isNotEmpty() == true) {
                    coil.compose.AsyncImage(
                        model = detail.backdropPaths.first(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
                // Back button
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = detail?.name ?: "",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (detail?.rating != null) {
                        Surface(
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
                            color = PurpleAccent.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "★ ${detail.rating}",
                                style = MaterialTheme.typography.labelMedium,
                                color = PurpleAccent,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    if (detail?.genre != null) {
                        Surface(
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
                            color = CyanAccent.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = detail.genre,
                                style = MaterialTheme.typography.labelMedium,
                                color = CyanAccent,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Play button
                Button(
                    onClick = {
                        val url = uiState.streamUrl
                        navController.navigate("player?url=${java.net.URLEncoder.encode(url, "UTF-8")}&title=${java.net.URLEncoder.encode(detail?.name ?: "", "UTF-8")}")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = BackgroundDark
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reproducir", color = BackgroundDark)
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (detail?.description != null) {
                    Text(
                        text = "Sinopsis",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = detail.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                if (detail?.cast != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Reparto",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = detail.cast,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                if (detail?.director != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Director: ${detail.director}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
