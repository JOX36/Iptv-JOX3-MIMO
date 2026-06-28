package com.jox3.tv.ui.series

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.jox3.tv.domain.model.Episode
import com.jox3.tv.domain.model.Season
import com.jox3.tv.ui.theme.*

@Composable
fun SeriesDetailScreen(
    navController: NavController,
    viewModel: SeriesDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        if (uiState.isLoading) {
            CircularProgressIndicator(color = CyanAccent, modifier = Modifier.align(Alignment.Center))
        } else if (uiState.series != null) {
            val series = uiState.series!!
            val selectedSeason = uiState.selectedSeason
            val episodes = series.episodes[selectedSeason] ?: emptyList()

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                // Header with cover
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                        if (series.cover != null) {
                            AsyncImage(model = series.cover, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        } else {
                            Box(Modifier.fillMaxSize().background(SurfaceDark), contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.Tv, null, tint = TextTertiary, modifier = Modifier.size(64.dp))
                            }
                        }
                        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, BackgroundDark.copy(0.7f), BackgroundDark))))
                        IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.padding(16.dp).statusBarsPadding()) {
                            Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color.Black.copy(0.5f)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.ArrowBack, "Volver", tint = Color.White)
                            }
                        }
                    }
                }

                // Title & info
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Text(series.name, fontSize = 24.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            series.rating?.let {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Star, null, tint = WarningYellow, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(it, fontWeight = FontWeight.Bold, color = WarningYellow, fontSize = 14.sp)
                                }
                            }
                            series.genre?.let {
                                Surface(color = SurfaceVariantDark, shape = RoundedCornerShape(8.dp)) {
                                    Text(it, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 12.sp, color = TextSecondary)
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        series.plot?.let {
                            Text(it, fontSize = 13.sp, color = TextSecondary, lineHeight = 20.sp, maxLines = 4, overflow = TextOverflow.Ellipsis)
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }

                // Season selector
                item {
                    Text("Temporadas", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary, modifier = Modifier.padding(horizontal = 20.dp))
                    Spacer(Modifier.height(8.dp))
                    LazyRow(
                        modifier = Modifier.padding(start = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(series.seasons) { season ->
                            SeasonChip(
                                season = season,
                                isSelected = season.seasonNumber == selectedSeason,
                                onClick = { viewModel.selectSeason(season.seasonNumber) }
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }

                // Episodes
                item {
                    Text("Episodios", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary, modifier = Modifier.padding(horizontal = 20.dp))
                    Spacer(Modifier.height(8.dp))
                }

                items(episodes) { episode ->
                    EpisodeItem(
                        episode = episode,
                        onClick = {
                            val ext = episode.containerExtension ?: "mp4"
                            val url = "${uiState.seriesBaseUrl}${episode.id}.$ext"
                            navController.navigate("player?url=${java.net.URLEncoder.encode(url, "UTF-8")}&title=${java.net.URLEncoder.encode("${series.name} - ${episode.title}", "UTF-8")}")
                        }
                    )
                }

                item { Spacer(Modifier.height(100.dp)) }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Serie no encontrada", color = TextSecondary) }
        }
    }
}

@Composable
private fun SeasonChip(season: Season, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        color = if (isSelected) CyanAccent else SurfaceDark,
        shape = RoundedCornerShape(12.dp),
        border = if (!isSelected) ButtonDefaults.outlinedButtonBorder else null
    ) {
        Text(
            season.name ?: "T${season.seasonNumber}",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) Color.White else TextSecondary
        )
    }
}

@Composable
private fun EpisodeItem(episode: Episode, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Episode thumbnail
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceVariantDark),
                contentAlignment = Alignment.Center
            ) {
                if (episode.image != null) {
                    AsyncImage(model = episode.image, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Icon(Icons.Outlined.PlayCircle, null, tint = CyanAccent.copy(0.5f), modifier = Modifier.size(24.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "E${episode.episodeNum} · ${episode.title}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                episode.duration?.let {
                    Text(it, fontSize = 11.sp, color = TextTertiary)
                }
                episode.plot?.let {
                    Text(it, fontSize = 11.sp, color = TextTertiary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            if (episode.rating != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, null, tint = WarningYellow, modifier = Modifier.size(12.dp))
                    Text("${episode.rating}", fontSize = 11.sp, color = WarningYellow, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}


