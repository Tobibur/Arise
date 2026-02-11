package com.tobibur.subalarm.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SubAlarmItemCard(
    modifier: Modifier,
    title: String,
    time: String,
    isActive: Boolean,
    onSwitchChange: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(Modifier.width(16.dp))
            Icon(imageVector = Icons.Default.AlarmAdd, contentDescription = "Sub Alarm icon")
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = time,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(Modifier.weight(1f))
            SwitchWithIcon(
                modifier = Modifier.scale(0.7f),
                checked = isActive
            ) {
                onSwitchChange(it)
            }
            Spacer(Modifier.width(16.dp))
        }
    }
}

@Preview
@Composable
fun SubAlarmItemCardPreview() {
    SubAlarmItemCard(
        modifier = Modifier,
        title = "Sub Alarm Title",
        time = "+10 mins (12:00 AM)",
        isActive = false,
        onSwitchChange = {}
    ) {}
}