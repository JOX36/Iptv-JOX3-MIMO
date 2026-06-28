package com.jox3.tv.ui.live

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
fun LiveScreen(
    navController: NavController,
    viewModel: LiveViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Top bar
        TopAppBar(
            title = {
                Text("TV en Vivo", color = TextPrimary)
            },
            actions = {
                IconButton(onClick = { navController.navigate("epg") }) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = "EPG", tint = CyanAccent)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = BackgroundDark,
                titleContentColor = TextPrimary
            )
        )

        // Search
        Jox3SearchBar(
            query = uiState.searchQuery,
            onQueryChange = { viewModel.updateSearch(it) },
            placeholder = "Buscar canales...",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Category chips
        CategoryChips(
            categories = uiState.categories,
            selectedId = uiState.selectedCategoryId,
            onCategorySelected = { viewModel.selectCategory(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.isLoading) {
            LoadingIndicator()
        } else if (uiState.filteredChannels.isEmpty()) {
            EmptyState(message = "No se encontraron canales")
        } else {
            // Channel grid
            val columns = when {
                uiState.filteredChannels.size > 20 -> 4
                uiState.filteredChannels.size > 10 -> 3
                else -> 2
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.filteredChannels) { channel ->
                    val program = uiState.currentPrograms[channel.epgChannelId]
                    ChannelCard(
                        name = channel.name,
                        icon = channel.icon,
                        currentProgram = program?.title,
                        isFavorite = channel.isFavorite,
                        onClick = {
                            val url = uiState.serverConfig?.let {
                                "${it.streamBaseUrl}${channel.streamId}.m3u8"
                            } ?: return@ChannelCard
                            navController.navigate("player?url=${java.net.URLEncoder.encode(url, "UTF-8")}&title=${java.net.URLEncoder.encode(channel.name, "UTF-8")}")
                        },
                        onFavoriteClick = { viewModel.toggleFavorite(channel.streamId) }
                    )
                }
            }
        }
    }
}
