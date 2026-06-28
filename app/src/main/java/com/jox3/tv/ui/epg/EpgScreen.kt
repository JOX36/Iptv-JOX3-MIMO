package com.jox3.tv.ui.epg

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jox3.tv.domain.model.EpgProgram
import com.jox3.tv.domain.model.LiveChannel
import com.jox3.tv.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpgScreen(
    navController: NavController,
    viewModel: EpgViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Guía TV", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Volver", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark, titleContentColor = TextPrimary)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CyanAccent)
            }
        } else if (uiState.channels.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.CalendarMonth, null, tint = TextTertiary, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("No hay datos de EPG", color = TextSecondary, fontSize = 16.sp)
                    Text("Conecta un servidor para ver la guía", color = TextTertiary, fontSize = 13.sp)
                }
            }
        } else {
            // Time header + channel rows
            val timeSlots = remember(uiState.epgData) { generateTimeSlots() }
            val selectedChannel = uiState.selectedChannelId

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Time header row
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(start = 120.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
                    ) {
                        timeSlots.forEach { slot ->
                            Box(
                                modifier = Modifier
                                    .width(120.dp)
                                    .padding(horizontal = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(slot)),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = SurfaceVariantDark, thickness = 1.dp)
                }

                // Channel rows
                items(uiState.channels) { channel ->
                    val isExpanded = selectedChannel == channel.streamId.toString()
                    val programs = uiState.epgData[channel.epgChannelId] ?: emptyList()

                    EpgChannelRow(
                        channel = channel,
                        programs = programs,
                        timeSlots = timeSlots,
                        isExpanded = isExpanded,
                        onChannelClick = {
                            viewModel.selectChannel(
                                if (isExpanded) null else channel.streamId.toString()
                            )
                        },
                        onPlayClick = {
                            val url = channel.streamUrl
                            navController.navigate("player?url=${java.net.URLEncoder.encode(url, "UTF-8")}&title=${java.net.URLEncoder.encode(channel.name, "UTF-8")}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EpgChannelRow(
    channel: LiveChannel,
    programs: List<EpgProgram>,
    timeSlots: List<Long>,
    isExpanded: Boolean,
    onChannelClick: () -> Unit,
    onPlayClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onChannelClick)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Channel info
            Row(
                modifier = Modifier.width(120.dp).padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Channel icon placeholder
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceVariantDark),
                    contentAlignment = Alignment.Center
                ) {
                    if (channel.icon != null) {
                        // Load image with coil if available
                        Icon(Icons.Outlined.Tv, null, tint = CyanAccent, modifier = Modifier.size(18.dp))
                    } else {
                        Icon(Icons.Outlined.Tv, null, tint = TextTertiary, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        channel.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (programs.isNotEmpty()) {
                        val current = programs.firstOrNull { it.isCurrentlyPlaying }
                        if (current != null) {
                            Text(
                                current.title,
                                fontSize = 10.sp,
                                color = CyanAccent,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // EPG program blocks
            Box(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState())
            ) {
                Row {
                    timeSlots.forEach { slotStart ->
                        val slotEnd = slotStart + 2 * 60 * 60 * 1000 // 2h slots
                        val slotPrograms = programs.filter { it.startTime < slotEnd && it.endTime > slotStart }

                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(if (isExpanded) 60.dp else 36.dp)
                                .padding(1.dp)
                        ) {
                            if (slotPrograms.isNotEmpty()) {
                                val program = slotPrograms.first()
                                val categoryIndex = (program.category.hashCode() and 0x7FFFFFFF) % CategoryColors.size
                                val color = CategoryColors[categoryIndex]

                                Card(
                                    modifier = Modifier.fillMaxSize(),
                                    colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            program.title,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = color,
                                            maxLines = if (isExpanded) 2 else 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (isExpanded && program.description.isNotBlank()) {
                                            Text(
                                                program.description,
                                                fontSize = 8.sp,
                                                color = TextTertiary,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Play button
            IconButton(onClick = onPlayClick, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Filled.PlayCircle, null, tint = CyanAccent, modifier = Modifier.size(22.dp))
            }
        }

        // Expanded: show full program list
        if (isExpanded && programs.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                val current = programs.firstOrNull { it.isCurrentlyPlaying }
                val upcoming = programs.filter { it.startTime > System.currentTimeMillis() }.take(5)

                if (current != null) {
                    Text("Ahora", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                    EpgProgramItem(current, isCurrent = true)
                    Spacer(Modifier.height(8.dp))
                }

                if (upcoming.isNotEmpty()) {
                    Text("Próximos", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    upcoming.forEach { program ->
                        EpgProgramItem(program, isCurrent = false)
                    }
                }
            }
        }

        HorizontalDivider(color = SurfaceVariantDark.copy(alpha = 0.5f), thickness = 0.5.dp)
    }
}

@Composable
private fun EpgProgramItem(program: EpgProgram, isCurrent: Boolean) {
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            "${timeFormat.format(Date(program.startTime))} - ${timeFormat.format(Date(program.endTime))}",
            fontSize = 11.sp,
            color = if (isCurrent) CyanAccent else TextTertiary,
            modifier = Modifier.width(100.dp)
        )
        Column {
            Text(
                program.title,
                fontSize = 12.sp,
                fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isCurrent) TextPrimary else TextSecondary
            )
            if (program.description.isNotBlank()) {
                Text(
                    program.description,
                    fontSize = 10.sp,
                    color = TextTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun generateTimeSlots(): List<Long> {
    val now = System.currentTimeMillis()
    val cal = Calendar.getInstance().apply {
        timeInMillis = now
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.HOUR, -2)
    }
    return (0..12).map {
        val time = cal.timeInMillis
        cal.add(Calendar.HOUR, 2)
        time
    }
}
