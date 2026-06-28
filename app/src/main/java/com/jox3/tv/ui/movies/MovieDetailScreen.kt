package com.jox3.tv.ui.movies

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.jox3.tv.ui.theme.*

@Composable
fun MovieDetailScreen(
    navController: NavController,
    viewModel: MovieDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        if (uiState.isLoading) {
            CircularProgressIndicator(color = CyanAccent, modifier = Modifier.align(Alignment.Center))
        } else if (uiState.movie != null) {
            val movie = uiState.movie!!
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                // Backdrop
                Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                    if (movie.image != null) {
                        AsyncImage(model = movie.image, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Box(Modifier.fillMaxSize().background(SurfaceDark), contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.Movie, null, tint = TextTertiary, modifier = Modifier.size(64.dp))
                        }
                    }
                    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, BackgroundDark.copy(0.6f), BackgroundDark))))
                    IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.padding(16.dp).statusBarsPadding()) {
                        Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color.Black.copy(0.5f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.ArrowBack, "Volver", tint = Color.White)
                        }
                    }
                }

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(movie.name, fontSize = 24.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (movie.rating != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Star, null, tint = WarningYellow, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(movie.rating!!, fontWeight = FontWeight.Bold, color = WarningYellow, fontSize = 14.sp)
                            }
                        }
                        movie.genre?.let { MetaChip(it) }
                        movie.duration?.let { MetaChip(it) }
                        movie.releaseDate?.let { MetaChip(it) }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Play button
                    Button(
                        onClick = {
                            uiState.streamUrl?.let { url ->
                                navController.navigate("player?url=${java.net.URLEncoder.encode(url, "UTF-8")}&title=${java.net.URLEncoder.encode(movie.name, "UTF-8")}")
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(CyanAccent, PurpleAccent)), RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.PlayArrow, null, tint = Color.White)
                                Spacer(Modifier.width(8.dp))
                                Text("Reproducir", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    movie.description?.let {
                        Text("Sinopsis", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                        Spacer(Modifier.height(8.dp))
                        Text(it, fontSize = 14.sp, color = TextSecondary, lineHeight = 22.sp)
                        Spacer(Modifier.height(16.dp))
                    }

                    if (movie.director != null || movie.cast != null) {
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(16.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                movie.director?.let { InfoRow("Director", it); Spacer(Modifier.height(8.dp)) }
                                movie.cast?.let { InfoRow("Reparto", it) }
                            }
                        }
                    }
                    Spacer(Modifier.height(32.dp))
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Película no encontrada", color = TextSecondary) }
        }
    }
}

@Composable
private fun MetaChip(text: String) {
    Surface(color = SurfaceVariantDark, shape = RoundedCornerShape(8.dp)) {
        Text(text, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = CyanAccent)
        Text(value, fontSize = 13.sp, color = TextSecondary, lineHeight = 18.sp)
    }
}
