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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesDetailScreen(
    navController: NavController,
    viewModel: SeriesDetailViewModel = hiltViewModel()
) {
    val navBackStackEntry = navController.currentBackStackEntry
    val seriesId = navBackStackEntry?.arguments?.getInt("seriesId") ?: 0
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(seriesId) {
        viewModel.loadDetail(seriesId)
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
        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(SurfaceVariantDark)
            ) {
                if (detail?.cover != null) {
                    AsyncImage(
                        model = detail.cover,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        BackgroundDark.copy(alpha = 0.3f),
                                        BackgroundDark
                                    )
                                )
                            )
                    )
                }
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = detail?.name ?: "",
                        style = MaterialTheme.typography.headlineLarge,
                        color = TextPrimary
                    )
                }
            }
        }

        // Info
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (detail?.rating != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
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
                            shape = RoundedCornerShape(6.dp),
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

                if (detail?.plot != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = detail.plot,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Seasons
        if (detail?.seasons?.isNotEmpty() == true) {
            item {
                Text(
                    text = "Temporadas",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(detail.seasons) { season ->
                        FilterChip(
                            selected = uiState.selectedSeason == season.seasonNumber,
                            onClick = { viewModel.selectSeason(season.seasonNumber) },
                            label = { Text(season.name ?: "T${season.seasonNumber}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanAccent.copy(alpha = 0.2f),
                                selectedLabelColor = CyanAccent,
                                containerColor = SurfaceVariantDark,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = SurfaceVariantDark,
                                selectedBorderColor = CyanAccent.copy(alpha = 0.5f),
                                enabled = true,
                                selected = uiState.selectedSeason == season.seasonNumber
                            )
                        )
                    }
                }
            }
        }

        // Episodes
        val episodes = detail?.episodes?.get(uiState.selectedSeason) ?: emptyList()
        if (episodes.isNotEmpty()) {
            item {
                Text(
                    text = "Episodios",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            items(episodes) { episode ->
                EpisodeCard(
                    episode = episode,
                    onClick = {
                        val url = viewModel.getEpisodeStreamUrl(episode.id, episode.containerExtension)
                        navController.navigate("player?url=${java.net.URLEncoder.encode(url, "UTF-8")}&title=${java.net.URLEncoder.encode("${detail?.name} - ${episode.title}", "UTF-8")}")
                    }
                )
            }
        }
    }
}

@Composable
private fun EpisodeCard(
    episode: com.jox3.tv.domain.model.Episode,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Episode image or number
            Box(
                modifier = Modifier
                    .size(80.dp, 50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceVariantDark),
                contentAlignment = Alignment.Center
            ) {
                if (episode.image != null) {
                    AsyncImage(
                        model = episode.image,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "E${episode.episodeNum}",
                        style = MaterialTheme.typography.titleMedium,
                        color = CyanAccent
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = episode.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (episode.plot != null) {
                    Text(
                        text = episode.plot,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.PlayCircle,
                contentDescription = "Play",
                tint = CyanAccent,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
