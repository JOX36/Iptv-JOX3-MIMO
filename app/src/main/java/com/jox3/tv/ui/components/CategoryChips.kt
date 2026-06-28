package com.jox3.tv.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jox3.tv.domain.model.Category
import com.jox3.tv.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryChips(
    categories: List<Category>,
    selectedId: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        item {
            FilterChip(
                selected = selectedId == null,
                onClick = { onCategorySelected(null) },
                label = { Text("Todos") },
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
                    selected = selectedId == null
                )
            )
        }
        items(categories) { category ->
            FilterChip(
                selected = selectedId == category.id,
                onClick = { onCategorySelected(category.id) },
                label = { Text(category.name) },
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
                    selected = selectedId == category.id
                )
            )
        }
    }
}
