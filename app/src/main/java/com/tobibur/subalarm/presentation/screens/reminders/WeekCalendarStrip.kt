package com.tobibur.subalarm.presentation.screens.reminders

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
internal fun MonthHeader(
    dateMillis: Long,
    onMonthClick: () -> Unit
) {
    val monthYear = remember(dateMillis) {
        SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date(dateMillis))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onMonthClick)
        ) {
            Text(
                text = monthYear,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Change month",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
internal fun WeekStrip(
    selectedDateMillis: Long,
    todayMillis: Long,
    onDateSelected: (Long) -> Unit
) {
    val centerPage = 500
    val totalPages = centerPage * 2 + 1

    val thisWeekMonday = remember(todayMillis) { getWeekDays(todayMillis).first() }

    fun weekPageForDate(dateMillis: Long): Int {
        val monday = getWeekDays(dateMillis).first()
        val offset = ((monday - thisWeekMonday) / (7 * DAY_MILLIS)).toInt()
        return centerPage + offset
    }

    fun mondayForPage(page: Int): Long {
        val weekOffset = page - centerPage
        return Calendar.getInstance().apply {
            timeInMillis = thisWeekMonday
            add(Calendar.DAY_OF_MONTH, weekOffset * 7)
        }.timeInMillis
    }

    val targetPage = weekPageForDate(selectedDateMillis)
    val pagerState = rememberPagerState(initialPage = targetPage) { totalPages }

    // Sync pager when selectedDate changes externally (e.g., from date picker)
    LaunchedEffect(targetPage) {
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    // When user swipes to a new week, select Monday of that week
    val currentSelectedDateMillis by rememberUpdatedState(selectedDateMillis)
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            if (page != weekPageForDate(currentSelectedDateMillis)) {
                onDateSelected(mondayForPage(page))
            }
        }
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            DAY_LABELS.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { page ->
            val weekDays = remember(page) { getWeekDays(mondayForPage(page)) }
            Row(modifier = Modifier.fillMaxWidth()) {
                weekDays.forEach { dayMillis ->
                    val dayNumber = Calendar.getInstance().apply { timeInMillis = dayMillis }
                        .get(Calendar.DAY_OF_MONTH)
                    val isSelected = isSameDay(dayMillis, selectedDateMillis)
                    val isToday = isSameDay(dayMillis, todayMillis)
                    val highlighted = isSelected || isToday

                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .then(
                                    if (highlighted) Modifier.background(
                                        MaterialTheme.colorScheme.primary, CircleShape
                                    ) else Modifier
                                )
                                .clickable { onDateSelected(dayMillis) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayNumber.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (highlighted) FontWeight.Bold else FontWeight.Normal,
                                color = if (highlighted) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun DateSectionHeader(dateMillis: Long, todayMillis: Long) {
    val text = remember(dateMillis, todayMillis) {
        val date = Date(dateMillis)
        val monthDay = SimpleDateFormat("MMM d", Locale.getDefault()).format(date).uppercase()
        val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(date).uppercase()
        val relative = when {
            isSameDay(dateMillis, todayMillis) -> " \u2022 TODAY"
            isSameDay(dateMillis, todayMillis + DAY_MILLIS) -> " \u2022 TOMORROW"
            else -> ""
        }
        "$monthDay$relative \u2022 $dayName"
    }

    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}
