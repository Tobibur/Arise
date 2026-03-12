package com.tobibur.arise.presentation.screens.reminders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

internal data class RepeatOption(val label: String, val daysBitmask: Int)

internal fun buildRepeatOptions(selectedDateMillis: Long): List<RepeatOption> {
    val cal = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
    val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
    val bitIndex = (dayOfWeek + 5) % 7
    val dayName = SimpleDateFormat("EEE", Locale.getDefault()).format(cal.time)

    return listOf(
        RepeatOption("None", 0),
        RepeatOption("Every day", 0b1111111),
        RepeatOption("Every week on $dayName", 1 shl bitIndex),
        RepeatOption("Every weekday (Mon - Fri)", 0b0011111),
        RepeatOption("Every weekend (Sat - Sun)", 0b1100000),
    )
}

private fun repeatLabel(repeatDays: Int, selectedDateMillis: Long): String {
    return buildRepeatOptions(selectedDateMillis)
        .find { it.daysBitmask == repeatDays }?.label ?: "Custom"
}

private const val CUSTOM_SENTINEL = -1

@Composable
internal fun RepeatOptionSelector(
    repeatDays: Int,
    selectedDateMillis: Long,
    isCustom: Boolean,
    onRepeatChanged: (days: Int, isCustom: Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = remember(selectedDateMillis) {
        buildRepeatOptions(selectedDateMillis) + RepeatOption("Custom", CUSTOM_SENTINEL)
    }
    val currentLabel = if (isCustom) "Custom" else remember(repeatDays, selectedDateMillis) {
        repeatLabel(repeatDays, selectedDateMillis)
    }

    Column {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Repeat,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Repeat",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = currentLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.label) },
                        onClick = {
                            if (option.daysBitmask == CUSTOM_SENTINEL) {
                                onRepeatChanged(repeatDays, true)
                            } else {
                                onRepeatChanged(option.daysBitmask, false)
                            }
                            expanded = false
                        }
                    )
                }
            }
        }

        AnimatedVisibility(visible = isCustom) {
            Column {
                Spacer(Modifier.height(8.dp))
                CustomDaySelector(
                    repeatDays = repeatDays,
                    onToggle = { index ->
                        onRepeatChanged(repeatDays xor (1 shl index), true)
                    }
                )
            }
        }
    }
}

@Composable
private fun CustomDaySelector(
    repeatDays: Int,
    onToggle: (index: Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DAY_LABELS.forEachIndexed { index, label ->
            val selected = repeatDays and (1 shl index) != 0
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant,
                        CircleShape
                    )
                    .clickable { onToggle(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    color = if (selected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
