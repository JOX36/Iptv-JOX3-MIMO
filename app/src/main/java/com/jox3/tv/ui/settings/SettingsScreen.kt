package com.jox3.tv.ui.settings

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jox3.tv.domain.model.ServerConfig
import com.jox3.tv.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Server accounts section
            item { SectionHeader("Servidores", Icons.Outlined.Dns) }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("login") },
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(listOf(CyanAccent.copy(0.3f), PurpleAccent.copy(0.3f)))
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CyanAccent.copy(0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Add, null, tint = CyanAccent, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text("Añadir servidor", fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text("Conectar nueva cuenta Xtream Codes", fontSize = 12.sp, color = TextTertiary)
                        }
                    }
                }
            }

            items(uiState.accounts) { account ->
                ServerAccountCard(
                    account = account,
                    onActivate = { viewModel.activateAccount(account.id) },
                    onDelete = { showDeleteDialog = account.id }
                )
            }

            // Player section
            item { Spacer(modifier = Modifier.height(8.dp)); SectionHeader("Reproductor", Icons.Outlined.PlayCircle) }

            item {
                SettingsToggle("Hardware decoding", "Usar aceleración por hardware", Icons.Outlined.Memory, uiState.hardwareDecoding) { viewModel.setHardwareDecoding(it) }
            }
            item {
                SettingsToggle("Auto-reproducir", "Reproducir automáticamente al seleccionar", Icons.Outlined.PlayArrow, uiState.autoPlay) { viewModel.setAutoPlay(it) }
            }

            // EPG section
            item { Spacer(modifier = Modifier.height(8.dp)); SectionHeader("Guía TV (EPG)", Icons.Outlined.CalendarMonth) }

            item {
                SettingsToggle("Cargar EPG automáticamente", "Actualizar guía al iniciar la app", Icons.Outlined.Update, uiState.autoLoadEpg) { viewModel.setAutoLoadEpg(it) }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(PurpleAccent.copy(0.1f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.DeleteSweep, null, tint = PurpleAccent, modifier = Modifier.size(22.dp))
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Limpiar caché EPG", fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text("${uiState.epgCacheSize} MB ocupados", fontSize = 12.sp, color = TextTertiary)
                        }
                        TextButton(onClick = { viewModel.clearEpgCache() }) { Text("Limpiar", color = ErrorRed) }
                    }
                }
            }

            // About section
            item { Spacer(modifier = Modifier.height(8.dp)); SectionHeader("Acerca de", Icons.Outlined.Info) }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(Brush.linearGradient(listOf(CyanAccent, PurpleAccent))), contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.PlayArrow, null, tint = Color.White, modifier = Modifier.size(26.dp))
                            }
                            Spacer(Modifier.width(14.dp))
                            Column {
                                Text("JOX3 TV", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                                Text("v1.0.0", fontSize = 13.sp, color = TextSecondary)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("Reproductor IPTV con soporte Xtream Codes, EPG completo y multi-lista.", fontSize = 13.sp, color = TextTertiary, lineHeight = 18.sp)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }

    showDeleteDialog?.let { accountId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            containerColor = SurfaceDark,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            title = { Text("Eliminar servidor") },
            text = { Text("¿Estás seguro? Se eliminará esta cuenta y todos los datos asociados.") },
            confirmButton = { TextButton(onClick = { viewModel.deleteAccount(accountId); showDeleteDialog = null }) { Text("Eliminar", color = ErrorRed) } },
            dismissButton = { TextButton(onClick = { showDeleteDialog = null }) { Text("Cancelar", color = TextSecondary) } }
        )
    }
}

@Composable
private fun SectionHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
        Icon(icon, null, tint = CyanAccent, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
    }
}

@Composable
private fun ServerAccountCard(account: ServerConfig, onActivate: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (account.isActive) CyanAccent.copy(0.08f) else SurfaceDark),
        shape = RoundedCornerShape(16.dp),
        border = if (account.isActive) CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CyanAccent.copy(0.4f), PurpleAccent.copy(0.2f))))) else null
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(if (account.isActive) CyanAccent.copy(0.15f) else SurfaceVariantDark), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Dns, null, tint = if (account.isActive) CyanAccent else TextTertiary, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(account.name.ifBlank { "${account.url}:${account.port}" }, fontWeight = FontWeight.SemiBold, color = TextPrimary, maxLines = 1)
                Text(account.username, fontSize = 12.sp, color = TextTertiary)
                if (account.isActive) Text("ACTIVO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
            }
            if (!account.isActive) TextButton(onClick = onActivate) { Text("Activar", color = CyanAccent, fontWeight = FontWeight.SemiBold) }
            IconButton(onClick = onDelete) { Icon(Icons.Outlined.Delete, null, tint = ErrorRed.copy(0.7f), modifier = Modifier.size(20.dp)) }
        }
    }
}

@Composable
private fun SettingsToggle(title: String, subtitle: String, icon: ImageVector, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = SurfaceDark), shape = RoundedCornerShape(16.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(SurfaceVariantDark), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = TextSecondary, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text(subtitle, fontSize = 12.sp, color = TextTertiary)
            }
            Switch(
                checked = checked, onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(checkedThumbColor = CyanAccent, checkedTrackColor = CyanAccent.copy(0.3f), uncheckedThumbColor = TextTertiary, uncheckedTrackColor = SurfaceVariantDark)
            )
        }
    }
}
