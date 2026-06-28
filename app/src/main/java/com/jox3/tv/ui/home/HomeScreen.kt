package com.jox3.tv.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.jox3.tv.ui.components.*
import com.jox3.tv.ui.theme.*

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (!uiState.hasAccount && !uiState.isLoading) {
        NoAccountView(navController = navController)
        return
    }

    if (uiState.isLoading) {
        LoadingIndicator()
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "JOX3 TV",
                        style = MaterialTheme.typography.displayMedium,
                        color = CyanAccent
                    )
                    Text(
                        text = "Tu entretenimiento, sin límites",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                IconButton(onClick = { navController.navigate("epg") }) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "EPG",
                        tint = CyanAccent
                    )
                }
            }
        }

        // Continue Watching
        if (uiState.recentChannels.isNotEmpty() || uiState.recentMovies.isNotEmpty()) {
            item {
                SectionHeader(title = "Continuar viendo")
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(uiState.recentChannels.take(5)) { channel ->
                        val program = uiState.currentPrograms[channel.epgChannelId]
                        ChannelCard(
                            name = channel.name,
                            icon = channel.icon,
                            currentProgram = program?.title,
                            isFavorite = channel.isFavorite,
                            onClick = {
                                viewModel.toggleFavorite(channel.streamId)
                            },
                            onFavoriteClick = { viewModel.toggleFavorite(channel.streamId) },
                            modifier = Modifier.width(160.dp)
                        )
                    }
                    items(uiState.recentMovies.take(5)) { movie ->
                        MovieCard(
                            name = movie.name,
                            icon = movie.icon,
                            rating = movie.rating,
                            isFavorite = movie.isFavorite,
                            onClick = {
                                navController.navigate("movie_detail/${movie.streamId}")
                            },
                            onFavoriteClick = {},
                            modifier = Modifier.width(150.dp)
                        )
                    }
                }
            }
        }

        // Favorites
        if (uiState.favoriteChannels.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Favoritos",
                    actionText = "Ver todos",
                    onActionClick = { navController.navigate("live") }
                )
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(uiState.favoriteChannels) { channel ->
                        val program = uiState.currentPrograms[channel.epgChannelId]
                        ChannelCard(
                            name = channel.name,
                            icon = channel.icon,
                            currentProgram = program?.title,
                            isFavorite = true,
                            onClick = {},
                            onFavoriteClick = { viewModel.toggleFavorite(channel.streamId) },
                            modifier = Modifier.width(160.dp)
                        )
                    }
                }
            }
        }

        // Quick Categories
        item {
            SectionHeader(title = "Explorar")
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickCategoryCard(
                    title = "TV en Vivo",
                    icon = Icons.Default.LiveTv,
                    color = CyanAccent,
                    onClick = { navController.navigate("live") },
                    modifier = Modifier.weight(1f)
                )
                QuickCategoryCard(
                    title = "Películas",
                    icon = Icons.Default.Movie,
                    color = PurpleAccent,
                    onClick = { navController.navigate("movies") },
                    modifier = Modifier.weight(1f)
                )
                QuickCategoryCard(
                    title = "Series",
                    icon = Icons.Default.Tv,
                    color = SuccessGreen,
                    onClick = { navController.navigate("series") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Current EPG preview
        if (uiState.currentPrograms.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Guía TV",
                    actionText = "Ver EPG",
                    onActionClick = { navController.navigate("epg") }
                )
            }
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.currentPrograms.entries.take(5).forEach { (channelId, program) ->
                        EpgPreviewCard(
                            channelId = channelId,
                            program = program
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NoAccountView(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Tv,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = CyanAccent
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Bienvenido a JOX3 TV",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Inicia sesión con tu cuenta Xtream Codes para comenzar a disfrutar",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { navController.navigate("login") },
            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Icon(Icons.Default.Login, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Iniciar Sesión", color = BackgroundDark)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = { navController.navigate("settings") },
            modifier = Modifier.fillMaxWidth(0.7f),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanAccent)
        ) {
            Text("Configurar Servidor")
        }
    }
}

@Composable
private fun QuickCategoryCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun EpgPreviewCard(
    channelId: String,
    program: com.jox3.tv.domain.model.EpgProgram
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(SuccessGreen)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = program.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = channelId,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
            }
            if (program.category.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = PurpleAccent.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = program.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = PurpleAccent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
